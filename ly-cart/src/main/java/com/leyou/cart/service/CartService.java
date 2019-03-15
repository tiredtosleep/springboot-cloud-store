package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class CartService {
    private static final String KEY_CART="cart:user:id:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     *  第一层Map，Key是用户id
     *  第二层Map，Key是购物车中商品id(skuid)，值是购物车数据
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取登入的用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key=KEY_CART+user.getId();
        //hashKey
        String hashKey = cart.getSkuId().toString();
        //绑定key
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断当前购物车商品是否存在
        if (operations.hasKey(hashKey)){
            //是，修改数量
            String json = operations.get(hashKey).toString();
            //将json转成cart类型
            Cart cachCart = JsonUtils.toBean(json, Cart.class);
            cachCart.setNum(cachCart.getNum()+cart.getNum());
            operations.put(hashKey,JsonUtils.toString(cachCart));
        }else {
            //否，新增
            operations.put(hashKey,JsonUtils.toString(cart));
        }
    }

    public List<Cart> queryCartList() {
        //获取登入的用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key=KEY_CART+user.getId();
        if (!redisTemplate.hasKey(key)){
            throw new LyException(ExceptionEnums.CART_NOT_FOUND);
        }
        //获取登入用户的所有购物车
        //绑定key
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> carts = operations.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());

        return carts;
    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取登入的用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key=KEY_CART+user.getId();
        //绑定key
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);

        if (!operations.hasKey(skuId.toString())){
            throw new LyException(ExceptionEnums.SKU_NOT_FOUND);
        }
        //通过skuId获取redis中购物车的商品信息
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);
        //保存到redis中
        operations.put(skuId.toString(),JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        //获取登入的用户
        UserInfo user = UserInterceptor.getUser();
        //key
        String key=KEY_CART+user.getId();
        //判断是否存在
        if (!redisTemplate.hasKey(key)){
            throw new LyException(ExceptionEnums.CART_NOT_FOUND);
        }

       redisTemplate.opsForHash().delete(key,skuId.toString());



    }
}
