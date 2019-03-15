package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:提供向外调用的接口
 * @author:cxg
 * @Date:${time}
 */
public interface BrandAPI {
    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("brand/{id}")
    public Brand queryBrandById(@PathVariable("id")Long id);
    /**
     * 根据ids集合查询品牌
     * @param ids
     * @return
     */
    @GetMapping("brand/list")
    public List<Brand> queryBrandByIds(@RequestParam("ids")List<Long> ids);
}
