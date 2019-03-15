package com.leyou.order.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@Table(name = "tb_order_detail")
public class OrderDetail {
    @Id

    @KeySql(useGeneratedKeys = true)private Long id;

    private Long orderId; // 订单1d

    private Long skuId;// 商品id

    private Integer num;//商品购买数量

    private String title;// 商品标题

    private Long price;// 商品单价

    private String ownSpec;// 商品规格数据

    private String image;// 图片

}
