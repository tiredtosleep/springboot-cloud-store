package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
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
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));

    }

    /**
     * 新增规格组
     *
     * @param
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(SpecGroup group) {
        specificationService.addGroup(group);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据id删除规格组
     *
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        specificationService.deleteGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 修改规格组
     *
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> editGroup(SpecGroup specGroup) {
        specificationService.editGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 通过规格组的gid查询规格参数
     * 通过商品分类cid查询规格参数
     * required=false：表示不传值的时候给null
     * @param gid 规格组id
     * @param cid 商品分类id
     * @param searching 是否用于搜索关键字
     * @param generic   是否是sku通用属性
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ) {
        return ResponseEntity.ok(specificationService.queryParamByList(gid,cid,searching,generic));
    }

    /**
     * 添加规格参数
     *
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addParam(SpecParam specParam) {
        specificationService.addParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 规格参数修改
     *
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> editParam(SpecParam specParam) {
        specificationService.editParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 根据id删除规格参数
     * @param id
     * @return
     */
    @DeleteMapping("param")
    public  ResponseEntity<Void> deleteParam(@PathVariable("id")Long id){
        specificationService.deleteParam(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 根据分类cid查询规格组
     * @param cid
     * @return
     */
    @GetMapping("group")
   public ResponseEntity<List<SpecGroup>>  queryListByCid(@RequestParam("cid")Long cid){
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }



}
