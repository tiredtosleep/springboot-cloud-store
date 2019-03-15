package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:把查询到的Spu转变为Goods来保存
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Service
public class SearchService {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //导入数据到ElasticSearch中
    public Goods buildGoods(Spu spu) {
        //1.查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //1.1.获取到分类的名字(查询集合的时候这样用)
        List<String> categoryName = categories.stream().map(Category::getName).collect(Collectors.toList());
        //2.查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //3.搜索字段
        String all = spu.getTitle()+ StringUtils.join(categoryName," ")+brand.getName();

        //4.查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuid(spu.getId());
        //4.1.处理sku
        List<Map<String, Object>> skus = new ArrayList<>();
        //4.2.处理价格
        List<Long>  priceList=new ArrayList<>();
        for (Sku sku : skuList) {
            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));//截取逗号之前的第一个
            skus.add(map);
            priceList.add(sku.getPrice());
        }

        //5.商品规格
        //5.1.查询规格参数
        List<SpecParam> params = specificationClient.queryParamByList(null, spu.getCid3(), true, null);

        //6.商品详情
        SpuDetail spuDetail = goodsClient.queryDetailBySpuId(spu.getId());
        //6.1.获取通用规格参数（参数类型为：Map<String, String>）
        String jsonGeneric = spuDetail.getGenericSpec();
        Map<Long, String> genericSpec = JsonUtils.toMap(jsonGeneric, Long.class, String.class);
        //6.2.获取特有规格参数 (参数类型为： Map<String, List<String>>这种类型)
        String jsonSpecial=spuDetail.getSpecialSpec();
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(jsonSpecial, new TypeReference<Map<Long, List<String>>>() {
        });

        //6.3.规格参数，key是规格参数的名字，值是规格参数的值
        Map<String,Object> specs=new HashMap<>();
        for (SpecParam param : params) {
            //规格名称
            String key = param.getName();
            Object value="";
            //判断是否通用规格参数
            if(param.getGeneric()){
                value=genericSpec.get(param.getId());
                if (param.getNumeric()){
                    //调用方法
                    value=chooseSegment(value.toString(),param);
                }
            }
            else {
                value=specialSpec.get(param.getId());
            }
            //存入Map中
            specs.put(key,value);
        }



        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);     // TODO 搜索字段，包含标题，分类，品牌 ，规格
        goods.setPrice(priceList); // TODO 所有sku的价格集合
        goods.setSkus(JsonUtils.toString(skus));  //TODO 所有sku的集合的json
        goods.setSpecs(specs); //TODO 所有的可搜索的规格参数
        return goods;
    }

    /**
     * 将下面进行分段
     * 0-500,500-1000,1000-1500,1500-2000,2500-
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public SearchResult search(SearchRequest request) {
        int page=request.getPage()-1;
        int size = request.getSize();

        //1、创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2、结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //3、分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //4、排序
        String sortBy=request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        //5、基本搜索条件,调用自己写的函数buildBasicQuery
       QueryBuilder basicQuery = buildBasicQuery(request);
        queryBuilder.withQuery(basicQuery);

        //6、聚合(分类和品牌)
        //6.1、聚合分类
        queryBuilder.addAggregation(AggregationBuilders.terms("categoryAggName").field("cid3"));
        //6.2、聚合品牌
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAggName").field("brandId"));


        //7、查询
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);

        //8、解析结果
        long total = result.getTotalElements();//总条数

        long totalPages =(total/size)+1;//总页数

        List<Goods> goodsList = result.getContent();//当前页结果
        //9、解析聚合结果
        Aggregations aggs = result.getAggregations();
        //9.1、查询品牌和分类
        List<Brand> brands=parseBrandAgg(aggs.get("brandAggName"));
        List<Category> categories=parseCategoryAgg(aggs.get("categoryAggName"));

        //10、规格参数聚合
        List<Map<String,Object>> specs=null;
        if (categories!=null&&categories.size()==1){
            //商品分类存在并且数量为1，可以聚合规格参数
            specs=buildSpecificationAgg(categories.get(0).getId(),basicQuery);
        }
        //11、返回结果
        return  new SearchResult(total,(long)totalPages,goodsList,categories,brands,specs);
    }

    /**
     * 抽取基本搜索函数
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件
        Map<String, String> map = request.getFilter();
        //map.entrySet().for
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //字段名
            String key = entry.getKey();
            if (!"cid3".equals(key)&&!"brandId".equals(key)){
                key="specs."+key+".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }

        return queryBuilder;
    }


    /**
     * 聚合规格参数
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        //1 查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamByList(null, cid, true, null);
        //2 聚合
        //2.1 创建构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        for (SpecParam param : params) {
            String name = param.getName();
            //聚合
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //3 获取结果
        AggregatedPage<Goods> result = this.elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //4 解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            //规格参数名称
            String name = param.getName();
            //聚合结果
            StringTerms terms = aggs.get(name);
            List<String> options = terms.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String, Object> map = new HashMap<>();
            map.put("k",name);
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    /**
     * 通过ids取查询分类
     * @param terms
     * @return
     */
    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        }catch (Exception e){
            log.error("分类错误信息",e);
            return null;
        }
    }

    /**
     * 通过ids取查询品牌
     * @param terms
     * @return
     */
    private List<Brand> parseBrandAgg(LongTerms terms) {
      try {
          List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

        List<Brand> brands = brandClient.queryBrandByIds(ids);
        return brands;
      }catch (Exception e){
          log.error("品牌错误信息",e);
          return null;
      }
    }

    /**
     * 处理mq消息(更新，修改)
     * @param spuId
     */
    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRepository.save(goods);
    }

    /**
     * 处理mq消息(删除)
     * @param spuId
     */
    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
