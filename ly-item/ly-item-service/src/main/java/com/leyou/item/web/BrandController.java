package com.leyou.item.web;

import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.pojo.Brand;

import com.leyou.item.service.BrandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     *根据当前页查询品牌，同时搜索查询品牌
     * @param page 当前页
     * @param rows 每页显示多少数据
     * @param sortBy 是否排序
     * @param desc 是否降序
     * @param key 搜索条件
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ){
        PageResult<Brand> brandPageResult = brandService.queryBrandByPage(page, rows, sortBy, desc, key);

        return ResponseEntity.ok(brandPageResult);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids")List<Long> cids){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 更新品牌
     * @param brand
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> editBrand(Brand brand){
        brandService.editBrand(brand);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据id删除品牌信息
     * @param bid
     * @return
     */
    @DeleteMapping("bid/{bid}")
     public ResponseEntity<Void> delete(@PathVariable("bid")Long bid){
            brandService.delete(bid);
        return ResponseEntity.status(HttpStatus.OK).build();
     }

    /**
     * 根据商品分类cid查询品牌分类
     * 涉及tb_brand，tb_category_brand表
     * @param cid
     * @return
     */
     @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
     }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
     @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
         return ResponseEntity.ok(brandService.queryById(id));
     }

    /**
     * 根据ids集合查询品牌
     * @param ids
     * @return
     */
     @GetMapping("list")
    public ResponseEntity<List<Brand>>queryBrandByIds(@RequestParam("ids")List<Long> ids){
         return ResponseEntity.ok(brandService.queryBrandByIds(ids));
     }


}
