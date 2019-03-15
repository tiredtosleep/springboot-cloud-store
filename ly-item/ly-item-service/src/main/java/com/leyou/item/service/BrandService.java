package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤(搜索)
        /**
         * SQL语句
         * where name like %x% or letter==x
         * order by desc
         */
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            //key.toUpperCase()变成大写
            example.createCriteria().orLike("name","%"+key+"%")
                    .orEqualTo("letter","%"+key+"%").orEqualTo("id","%"+key+"%");
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause=sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnums.BRAND_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(),list);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //将id设置成null，id是自增的
        brand.setId(null);
        //插入tb_brand
        int count = brandMapper.insert(brand);
        if (count!=1){
            throw new LyException(ExceptionEnums.BRAND_SAVE_ERROR);
        }
        //插入中间表tb_category_brand
        for (Long cid : cids) {
             count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count!=1){
                throw  new LyException(ExceptionEnums.BRAND_SAVE_ERROR);
            }
        }
    }

    /**
     * 根据bid删除品牌信息
     * 根据bid删除品牌和分类关联表
     * @param bid
     * @return
     */
    public void delete(Long bid) {
        brandMapper.deleteByPrimaryKey(bid);
        brandMapper.deleteByBid(bid);
    }

    /**
     * 更新品牌
     * @param brand
     * @return
     */
    public void editBrand(Brand brand) {
        int update = brandMapper.updateByPrimaryKeySelective(brand);
        if (update!=1){
            throw  new LyException(ExceptionEnums.BRAND_UPLOAD_ERROR);
        }
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand==null){
            throw  new LyException(ExceptionEnums.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     * 根据商品分类cid查询品牌分类
     * 涉及tb_brand，tb_category_brand表
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOUND);
        }
        return brands;
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOUND);
        }
        return brands;
    }
}
