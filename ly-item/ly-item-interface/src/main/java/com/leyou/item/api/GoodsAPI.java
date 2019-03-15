package com.leyou.item.api;

import com.leyou.common.utils.dto.CartDTO;
import com.leyou.common.utils.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:提供向外调用的接口
 * @author:cxg
 * @Date:${time}
 */
public interface GoodsAPI {
    /**
     * 根据spuid查询商品详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public SpuDetail queryDetailBySpuId(@PathVariable("id")Long spuId);


    /**
     * 根据spuid查询sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuid(@RequestParam("id")Long spuId);

    /**
     * 分页查询商品信息
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    @ResponseBody
    public PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id")Long id);

    /**
     * 根据sku的id集合查询sku
     *
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    List<Sku> querySkuByIds(@RequestParam("ids") List<Long> ids);
    /**
     * 减库存
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
     void decreaseStock(@RequestBody List<CartDTO> carts);
}
