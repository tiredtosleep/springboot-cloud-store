package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void>addCart(@RequestBody Cart cart){

        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> selectCart(){
        return ResponseEntity.ok(cartService.queryCartList());
    }

    /**
     * 修改购物车的商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("skuId")Long skuId,@RequestParam("num")Integer num){

        cartService.updateCartNum(skuId,num);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
