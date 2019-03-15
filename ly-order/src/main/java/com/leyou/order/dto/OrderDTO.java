package com.leyou.order.dto;

import com.leyou.common.utils.dto.CartDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: DTO数据传输
 * @author:cxg
 * @Date:${time}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull
    private Long addressId;// 收货人地址id
    @NotNull
    private Integer paymentType;// 付款方式
    @NotNull
    private List<CartDTO> carts;// 订单详情

}
