package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.utils.dto.CartDTO;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:商品货物
 * @author:cxg
 * @Date:${time}
 */
@Service
public class GoodsService {
    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private  BrandService brandService;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 分页查询商品信息
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索过滤字段
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spus = spuMapper.selectByExample(example);
        //判断
        if (CollectionUtils.isEmpty(spus)){
            throw  new LyException(ExceptionEnums.GOODS_NOT_FOUND);
        }
        //解析分类和品牌的名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spus);

        return new  PageResult<>(info.getTotal(),spus);
    }


    //解析分类和品牌的名称
    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //1.处理分类名称
            List<String> cname  = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            //1.1集合拼成字符串
            spu.setCname(StringUtils.join(cname,"/"));
            //2.处理品牌
            String bname = brandService.queryById(spu.getBrandId()).getName();
            spu.setBname(bname);
        }
    }
    /**
     * 新增商品信息
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     * @param spu
     * @return
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(true);

        int insert = spuMapper.insert(spu);
        if (insert!=1){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOUND);
        }

        //新增detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int insert1 = spuDetailMapper.insert(spuDetail);
        if (insert1!=1){
            throw new LyException(ExceptionEnums.SPU_ADD_ERROR);
        }

      //新增sku和stock
        saveSkuAndStock(spu);
        //发送mq消息
        amqpTemplate.convertAndSend("item.insert",spu.getId());

    }

    //新增sku和stock
    private void saveSkuAndStock(Spu spu) {
        //新增sku
        List<Stock> stockList=new ArrayList<>();
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {

            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int insert2 = skuMapper.insert(sku);
            if (insert2!=1){
                throw new LyException(ExceptionEnums.SKU_ADD_ERROR);
            }
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insert(stock);
        }

    }
    /**
     * 根据spuid查询商品详情(回显)
     * @param spuId
     * @return
     */
    public SpuDetail queryDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail==null){
            throw new LyException(ExceptionEnums.SPU_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }
    /**
     * 根据spuid查询sku(回显)
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuid(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        //查询库存
        List<Sku> skuLists = skuMapper.select(sku);
        for (Sku skuList : skuLists) {
            Stock stock = stockMapper.selectByPrimaryKey(skuList.getId());
            sku.setStock(stock.getStock());
        }
        return skuLists;
    }
    /**
     * 修改商品信息
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     * @param spu
     * @return
     */
    @Transactional
    public void uploadGoods(Spu spu) {
        if (spu.getId()==null){
            throw new LyException(ExceptionEnums.SPU_ID_NOT_NULL);
        }
        //查询sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除sku
            skuMapper.delete(sku);
            //获取sku的id集合
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            //删除stock
            stockMapper.deleteByIdList(ids);
        }
        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if(count!=1){
            throw new LyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(count!=1){
            throw new LyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //新增sku和stock
        saveSkuAndStock(spu);
        //发送mq消息
        amqpTemplate.convertAndSend("item.update",spu.getId());

    }
    /**
     * 通过spuId删除
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     * @param spuId
     * @return
     */
    @Transactional
    public void deleteGood(Long spuId)  {

        //查询sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        //查询到skuList
        List<Sku> skuList = skuMapper.select(sku);
        //判断skuList是否存在
        if (!CollectionUtils.isEmpty(skuList)){
            //删除sku
           skuMapper.delete(sku);
        }

        //得到sku的ids集合
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        //查询库存stocks
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        //判断是否存在
        if (!CollectionUtils.isEmpty(stocks)) {
            //删除stocks
            stockMapper.deleteByIdList(ids);
        }
        //查询商品详情
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail!=null) {
            //删除商品详情
            spuDetailMapper.deleteByPrimaryKey(spuId);
        }
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu!=null) {
            //删除spu
            spuMapper.deleteByPrimaryKey(spuId);
        }
        //发送mq消息
        amqpTemplate.convertAndSend("item.delete",spuId);
    }
    /**
     * 商品下架
     * @param spuId
     * @return
     */
    public void downGood(Long spuId) {
        Spu spu = new Spu();
        spu.setSaleable(false);
        spu.setId(spuId);
        spuMapper.updateByPrimaryKeySelective(spu);
    }
    /**
     * 商品上架
     * @param spuId
     * @return
     */
    public void upGood(Long spuId) {
        Spu spu = new Spu();
        spu.setSaleable(true);
        spu.setId(spuId);
        spuMapper.updateByPrimaryKeySelective(spu);
    }
    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            throw new LyException(ExceptionEnums.SPU_NOT_FOUND);
        }
        //查询sku调用上面的函数querySkuByid()
        spu.setSkus(querySkuBySpuid(id));
        //查询detail
       spu.setSpuDetail(queryDetailBySpuId(id));
        return spu;
    }

    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)){
            throw  new LyException(ExceptionEnums.SKU_NOT_FOUND);
        }
        //查询库存
        //填充库存
        loadStockInSku(ids, skus);
        return skus;
    }
    private void loadStockInSku(List<Long> ids, List<Sku> skus) {
        //批量查询库存
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnums.STOCK_NOT_FOUND);
        }
        //首先将库存转换为map，key为sku的ID
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));

        //遍历skus，并填充库存
        skus.forEach(s->s.setStock(stockMap.get(s.getId())));
    }

    public void decreaseStock(List<CartDTO> carts) {
        for (CartDTO cart : carts) {
            int count = stockMapper.decreaseStock(cart.getSkuId(), cart.getNum());
            if (count!=1){
                throw new LyException(ExceptionEnums.STOCK_NOT_ENOUGH);
            }
        }
    }
}
