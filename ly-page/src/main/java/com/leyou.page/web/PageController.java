package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class PageController {
    @Autowired
    private PageService  pageService;


    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model) {
        //查询模型数据
        Map<String,Object> attributes=pageService.loadModel(spuId);
        //准备模型属性
        model.addAllAttributes(attributes);
        //商品页面静态化
        pageService.createHtml(spuId);
        return "item";
    }

}

