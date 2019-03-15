package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:提供向外调用的接口
 * @author:cxg
 * @Date:${time}
 */
public interface CategoryAPI {
    /**
     * 根据ids集合查询商品分类
     * 不写实现直接写接口
     * @param ids
     * @return
     */
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids")List<Long> ids);
}
