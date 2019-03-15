package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    /**
     * 订单接口
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    /**
     * 创建支付链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.createPayUrl(orderId));
    }

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryStatusById(@PathVariable("id")Long orderId){
        return ResponseEntity.ok(orderService.queryStatusById(orderId).getValue());
    }
}
