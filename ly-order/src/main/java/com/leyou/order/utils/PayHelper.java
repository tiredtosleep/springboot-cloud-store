package com.leyou.order.utils;


import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

   @Autowired
    private WXPay wxPay;
   @Autowired
    private PayConfig config;
   @Autowired
   private OrderMapper orderMapper;
   @Autowired
   private OrderStatusMapper orderStatusMapper;




    public String createPayUrl(Long orderId, Long actualPay, String desc) {

        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //货币
            data.put("fee_type", "CNY");
            //金额，单位是分
            data.put("total_fee", actualPay.toString());
            //调用微信支付的终端IP（estore商城的IP）
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");
            //利用wxPay工具，完成下单
            Map<String, String> result = this.wxPay.unifiedOrder(data);

            //判断通信和业务标识（抽取方法）
            isSuccess(result);
            //下单成功，获取支付链接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("[微信支付] 创建交易订单异常", e);
            return null;
        }
    }

    /**
     * 判断通信和业务标识（抽取方法）
     * @param result
     */
    public void isSuccess(Map<String, String> result) {
        String returnCode = result.get("return_code");
        if (WXPayConstants.FAIL.equals(returnCode)) {
            //通信异常
            log.error("[微信下单] 微信下单通信失败，原因：", result.get("return_msg"));
            throw new LyException(ExceptionEnums.WX_PAY_ORDER_FAIL);

        }
        //判断业务标识
        String resultCode = result.get("result_code");
        if (WXPayConstants.FAIL.equals(resultCode)) {
            //通信异常
            log.error("[微信下单] 微信下业务信失败，原因：", result.get("err_code"), result.get("err_code_des"));
            throw new LyException(ExceptionEnums.WX_PAY_ORDER_FAIL);

        }
    }


    /**
     * 校验签名
     *
     * @param sign
     */
    public void isValidSign(Map<String, String> sign) {
        //重新生成签名
        try {//不确定用哪个加密，所有生成两个加密
            String sign1 = WXPayUtil.generateSignature(sign, config.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(sign, config.getKey(), WXPayConstants.SignType.MD5);
            // 和传过来签名校验
            String getSign = sign.get("sign");
            if (!StringUtils.equals(getSign,sign1)&& !StringUtils.equals(getSign,sign2)) {
                log.error("[支付回调] 支付订单参数有误，订单号：{}",sign.get("out_trade_no"));
                throw new LyException(ExceptionEnums.PAY_ORDER_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[支付回调] 支付订单参数有误，订单号：{}",sign.get("out_trade_no"));
            throw new LyException(ExceptionEnums.PAY_ORDER_PARAM_ERROR);
        }

    }

    public PayState queryPayStatus(Long orderId) {

        try {
            //组织请求参数
            Map<String, String> data = new HashMap<>();
            //订单号
            data.put("out_trade_no", orderId.toString());
            //查询状态
            Map<String, String> result = wxPay.orderQuery(data);
            // 通信和业务校验
            isSuccess(result);
            //校验签名
            isValidSign(result);

            //3 校验金额
            String totalFeeStr = result.get("total_fee");
            String outTradeNo = result.get("out_trade_no");
            if (StringUtils.isEmpty(totalFeeStr)|| StringUtils.isEmpty(outTradeNo)) {
                throw new  LyException(ExceptionEnums.INVALID_ORDER_PARAM);
            }
            //将totalFeeStr转成long,获取结果的金额totalFee
            Long totalFee = Long.valueOf(totalFeeStr);
            //获取订单中的金额

            Order order = orderMapper.selectByPrimaryKey(orderId);
            Long actualPay = order.getActualPay();
            //结果金额和订单金额是否一样
            if (!totalFee.equals(/*actualPay*/1)){
                throw new  LyException(ExceptionEnums.INVALID_ORDER_PARAM);
            }
            /**
             * 判断支付是否成功或失败
             * SUCCESS—支付成功
             *
             * REFUND—转入退款
             *
             * NOTPAY—未支付
             *
             * CLOSED—已关闭
             *
             * REVOKED—已撤销（付款码支付）
             *
             * USERPAYING--用户支付中（付款码支付）
             *
             * PAYERROR--支付失败
             */
            String state = result.get("trade_state");
            if (WXPayConstants.SUCCESS.equals(state)){
                //4 修改订单状态
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setStatus(OrderStatusEnum.PAYED.value());
                orderStatus.setOrderId(orderId);
                orderStatus.setPaymentTime(new Date());
                int i = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
                if (i!=1){
                    throw new  LyException(ExceptionEnums.UPDATE_ORDER_ERROR);
                }
                return PayState.SUCCESS;
            }
            if ("NOTPAY".equals(state)||"USERPAYING".equals(state)){
                return PayState.NOT_PAY;
            }
            return PayState.FAIL;


        } catch (Exception e) {
            return PayState.NOT_PAY;
        }


    }
}