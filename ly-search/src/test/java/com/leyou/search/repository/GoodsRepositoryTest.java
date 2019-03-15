package com.leyou.search.repository;

import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.bouncycastle.jcajce.provider.symmetric.IDEA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SearchService searchService;

    /**
     * 创建索引库
     */
    @Test
    public void testCreateIndex(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    /**
     * 将数据导入到索引库中
     */
    @Test
    public void  loadData(){
        int page=1;
        int rows=100;
        int size=0;



        //分页查询spu信息
        PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
        List<Spu> spuList = result.getItems();
        List<Goods> goodsList = new ArrayList<>();
        //构建goods
        for (Spu spu : spuList) {
            Goods goods = searchService.buildGoods(spu);
            goodsList.add(goods);
        }
        //存入索引库
            goodsRepository.saveAll(goodsList);


    }
}