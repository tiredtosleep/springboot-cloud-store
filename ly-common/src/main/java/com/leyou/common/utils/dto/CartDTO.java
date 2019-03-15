package com.leyou.common.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long skuId;// 商品skuId
    private Integer num;// 购买数量
}
