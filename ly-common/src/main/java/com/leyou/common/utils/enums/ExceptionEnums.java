package com.leyou.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnums {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空"),
    CATEGORY_NOT_FOND(404,"没查询到"),
    BRAND_NOT_FOUND(404,"品牌查不到或者不存在"),
    BRAND_SAVE_ERROR(404,"新增品牌失败" ),
    UPLOAD_FILE_ERROR(404,"文件上传失败"),
    Invalid_file_type(400,"无效文件类型" ),
    BRAND_CATEGORY_NOT_FOND(404,"品牌或者商品分类不存在"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没查到"),
    SPEC_GROUP_SAVE_ERROR(404,"新增规格组失败"),
    SPEC_GROUP_UPLOAD_ERROR(404,"更新规格组失败" ),
    SPEC_GROUP_DELETE_ERROR(404,"删除规格组失败" ),
    BRAND_UPLOAD_ERROR(404,"更新品牌" ),
    SPEC_PARAM_NOT_FOUND(404,"规格参数不存在"),
    SPEC_PARAM_ADD_ERROR(404,"添加规格参数失败"),
    SPEC_PARAM_UPLOAD_ERROR(404,"规格参数修改失败"),
    SPEC_PARAM_DELETE_ERROR(404,"删除规格参数失败"),
    GOODS_NOT_FOUND(404,"查询商品失败"),
    SPU_ADD_ERROR(500,"新增spu失败"),
    SKU_ADD_ERROR(500,"新增sku失败"),
    STOCK_ADD_ERROR(500,"新增库存失败"),
    SPU_DETAIL_NOT_FOUND(400,"商品详细未找到"),
    GOODS_UPDATE_ERROR(400,"修改商品失败"),
    SPU_ID_NOT_NULL(400,"商品id不能为空"),
    SPU_DELETE_ERROR(400,"删除商品信息失败"),
    SPU_NOT_FOUND(400,"spu未找到"),
    SKU_NOT_FOUND(400, "未找到sku"),
    INVALID_DATA_TYPE(400,"无效数据类型"),
    INVALID_USER_CODE(400,"验证码有误"),
    INVALID_USERNAME_PASSWORD(400,"参数有误"),
    CREATE_TOKEN_ERROR(500,"用户生成凭证失败"),
    UN_AUTHORIZED(401,"未授权" ),
    CREATE_NOT_FOUND(500,"创建订单失败"),
    CART_NOT_FOUND(404,"购物车不存在" ), STOCK_NOT_ENOUGH(500,"库存不足" ),
    STOCK_NOT_FOUND(404,"未找到库存"), ORDER_NOT_FOUND(404,"订单不存在" ), WX_PAY_ORDER_FAIL(404,"微信下单失败" ),
    ORDER_STATUS_ERROR(400,"订单状态异常" ), PAY_ORDER_PARAM_ERROR(400,"订单有误" ),
    INVALID_ORDER_PARAM(400,"订单有误" ),
    UPDATE_ORDER_ERROR(404,"更新订单失败" );
    private int code;
    private String msg;
}
