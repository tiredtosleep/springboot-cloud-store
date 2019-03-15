package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:
 * @author:cxg
 * @Date:${time}
 */
public interface SpecificationAPI {
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
    @GetMapping("spec/params")
  List<SpecParam> queryParamByList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    );

    /**
     * 根据分类cid查询规格组
     * @param cid
     * @return
     */
    @GetMapping("spec/group")
    List<SpecGroup> queryListByCid(@RequestParam("cid")Long cid);

}
