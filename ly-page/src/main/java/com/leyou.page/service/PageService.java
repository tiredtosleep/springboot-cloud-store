package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Service
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String,Object> loadModel(Long spuId) {
        Map<String,Object> model=new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询sku
        List<Sku> skus = spu.getSkus();
        //查询商品详情
        SpuDetail detail = spu.getSpuDetail();
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specs = specificationClient.queryListByCid(spu.getCid3());


        model.put("spu",spu);
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand", brand);
        model.put("categories",categories);
        model.put("specs",specs);
        return model;
    }

    /**
     * 创建静态文件
     * @param spuId
     */
    public void createHtml(Long spuId){
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流
        File file = new File("G:/Java-webspace/LeYou-store/leyou/ly-page/src/main/resources/templates", spuId + ".html");
        //判断是否存在
        if (file.exists()){
            file.delete();
        }
       try (PrintWriter writer=new PrintWriter(file,"utf-8")){
           //生成html
           templateEngine.process("item",context,writer);
       }catch (Exception e){
           log.error("[静态页服务]，生成静态页面异常！",e);
       }


    }

    /**
     * 删除静态页
     * @param spuId
     */
    public void deleteHtml(Long spuId) {
        File file = new File("G:/Java-webspace/LeYou-store/leyou/ly-page/src/main/resources/templates", spuId + ".html");
        if (file.exists()){
            file.delete();
        }
    }
}
