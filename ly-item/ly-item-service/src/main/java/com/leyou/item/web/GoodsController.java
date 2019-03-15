package com.leyou.item.web;

import com.leyou.common.utils.dto.CartDTO;
import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询商品信息
     *
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    @ResponseBody
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {
        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));
    }

    /**
     * 新增商品信息
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     *
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据spuid查询商品详情(回显)
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailBySpuId(@PathVariable("id") Long spuId) {
        return ResponseEntity.ok(goodsService.queryDetailBySpuId(spuId));

    }

    /**
     * 根据spuid查询sku(回显)
     *
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuid(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuBySpuid(spuId));
    }


    /**
     * 根据sku的id集合查询sku
     *
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }

    /**
     * 修改商品信息
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     *
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> uploadGoods(@RequestBody Spu spu) {
        goodsService.uploadGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 通过spuId删除
     * 涉及表：tb_spu，tb_sku，tb_stock，tb_spu_detail
     *
     * @param spuId
     * @return
     */
    @ResponseBody
    @PutMapping("spu/delete/{id}")
    public ResponseEntity<Void> deleteGood(@PathVariable("id") Long spuId) {
        goodsService.deleteGood(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 商品下架
     *
     * @param spuId
     * @return
     */
    @PostMapping("spu/down/{id}")
    public ResponseEntity<Void> downGood(@PathVariable("id") Long spuId) {
        goodsService.downGood(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 商品上架
     *
     * @param spuId
     * @return
     */
    @PutMapping("spu/up/{id}")
    public ResponseEntity<Void> upGood(@PathVariable("id") Long spuId) {
        goodsService.upGood(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据spu的id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {

        return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 减库存
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> carts){
        goodsService.decreaseStock(carts);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
