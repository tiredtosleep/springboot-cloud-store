package com.leyou.order.enums;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 支付状态码
 * @author:cxg
 * @Date:${time}
 */
public enum OrderStatusEnum {

    UN_PAY(1,"未付款"),
    PAYED(2,"已付款,未发货"),
    DELIVERED(3,"已发货,未确认"),
    SUCCESS(4,"交易成功"),
    CLOSED(1,"交易关闭"),
    RATED(1,"已评价"),
    ;
    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public Integer value(){
        return this.code;
    }


}
