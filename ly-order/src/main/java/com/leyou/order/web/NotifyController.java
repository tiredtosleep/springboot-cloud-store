package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /**
     * 微信支付成功回调
     * @param result
     * @return
     */
    @PostMapping(value = "pay",produces = "application/xml")
    public Map<String,String> hello(@RequestBody  Map<String,String> result){
        //处理回调
        orderService.handleNotify(result);
        log.info("[支付回调] 订单支付成功，订单号：{}",result.get("out_trade_no"));
        Map<String,String> msg=new HashMap<>();
        msg.put("return_code","SUCCESS");
        msg.put("retrun_msg","OK");
        return msg;
    }
}
