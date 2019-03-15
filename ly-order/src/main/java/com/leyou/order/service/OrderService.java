package com.leyou.order.service;

import ch.qos.logback.core.status.StatusManager;
import com.leyou.auth.entity.UserInfo;
import com.leyou.common.utils.IdWorker;
import com.leyou.common.utils.dto.CartDTO;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */

@Service
@Slf4j
public class OrderService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayHelper payHelper;


    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        // 1 新增订单
        Order order = new Order();
        // 1.1 订单编号，基本信息
        long orderId = idWorker.nextId();//雪花算法生成订单id
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        // 1.2 用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getName());
        order.setBuyerRate(false);

        // 1.3 收货人地址
        // 1.3.1 获取收货信息，这里是常量
        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());
        // 1.4 金额
        //付款金额相关，首先把orderDto转化成map，其中key为skuId,值为购物车中该sku的购买数量
        Map<Long, Integer> skuNumMap = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set<Long> ids = skuNumMap.keySet();
        //查询商品信息，根据skuIds批量查询sku详情
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));

        // 准备orderDetail（订单详情）集合
        List<OrderDetail> details = new ArrayList<>();

        long totalPay=0L;
        // 循环遍历计算价格
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * skuNumMap.get(sku.getId());
            // 封装orderDetail
            OrderDetail detail = new OrderDetail();
            // substringBefore取图片第一个
            detail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            detail.setNum(skuNumMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            details.add(detail);
        }
        //金额
        order.setTotalPay(totalPay);
        // 实付金额=总金额+邮费-优惠金额
        order.setActualPay(totalPay+order.getPostFee()-0);
        // 写入订单表order中
        int count = orderMapper.insertSelective(order);
        if (count!=1){
            log.error("[创建订单] 创建订单失败了 , orderId:",orderId);
            throw new LyException(ExceptionEnums.CREATE_NOT_FOUND);
        }
        // 2 新增订单详情
        count = orderDetailMapper.insertList(details);
        if (count!=details.size()){
            log.error("[创建订单详情] 创建订单详情失败了 , orderId:",orderId);
            throw new LyException(ExceptionEnums.CREATE_NOT_FOUND);
        }
        // 3 新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count!=1){
            log.error("[创建订单状态] 创建订单状态失败了 , orderId:",orderId);
            throw new LyException(ExceptionEnums.CREATE_NOT_FOUND);
        }
        // 4 减库存
        List<CartDTO> cartsDTOS = orderDTO.getCarts();
        goodsClient.decreaseStock(cartsDTOS);
        return orderId;
    }

    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null){
            throw new LyException(ExceptionEnums.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnums.ORDER_NOT_FOUND);
        }
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus==null){
            throw new LyException(ExceptionEnums.ORDER_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        order.setOrderDetails(details);
        return order;
    }

    public String createPayUrl(Long orderId) {
        //查询订单
        Order order = queryOrderById(orderId);
        //判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if (!status.equals(OrderStatusEnum.UN_PAY.value())){
            throw new LyException(ExceptionEnums.ORDER_STATUS_ERROR);
        }
        //支付金额
        Long actualPay = /*order.getActualPay()*/1L;
        //商品描述
        OrderDetail orderDetail = order.getOrderDetails().get(0);
        String desc = orderDetail.getTitle();

        return payHelper.createPayUrl(orderId,actualPay,desc);
    }

    public void handleNotify(Map<String, String> result) {

        //1 数据校验
        payHelper.isSuccess(result);
        //2 校验签名
        payHelper.isValidSign(result);
        //3 校验金额
        String totalFeeStr = result.get("total_fee");
        String outTradeNo = result.get("out_trade_no");
        if (StringUtils.isEmpty(totalFeeStr)|| StringUtils.isEmpty(outTradeNo)) {
            throw new  LyException(ExceptionEnums.INVALID_ORDER_PARAM);
        }
        //将totalFeeStr转成long,获取结果的金额totalFee
        Long totalFee = Long.valueOf(totalFeeStr);
        //获取订单中的金额
        Long orderId = Long.valueOf(outTradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Long actualPay = order.getActualPay();
        //结果金额和订单金额是否一样
        if (totalFee!=1){
            throw new  LyException(ExceptionEnums.INVALID_ORDER_PARAM);
        }
        //4 修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.PAYED.value());
        orderStatus.setOrderId(orderId);
        orderStatus.setPaymentTime(new Date());
        int i = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if (i!=1){
            throw new  LyException(ExceptionEnums.UPDATE_ORDER_ERROR);
        }
        log.info("[订单回调] ，订单支付成功",orderId);
    }

    public PayState queryStatusById(Long orderId) {
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        //判断是否支付
        if (!status.equals(OrderStatusEnum.UN_PAY.value())) {
            //如果已支付，真的是已支付
            return PayState.SUCCESS;
        }
        //如果未支付，但是其实不一定未支付，必须去微信查询状态
        return payHelper.queryPayStatus(orderId);
    }
}
