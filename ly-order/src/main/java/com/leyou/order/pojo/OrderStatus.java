package com.leyou.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@Table(name = "tb_order_status")
public class OrderStatus {
    @Id
    private Long orderId;

    private Integer status;

    private Date createTime;// 创建时间

    private Date paymentTime;// 付款时间

    private Date consignTime;// 发货时间

    private Date endTime;//交易结束时间

    private Date closeTime;// 交易关闭时间

    private Date commentTime;// 评价时间


}
