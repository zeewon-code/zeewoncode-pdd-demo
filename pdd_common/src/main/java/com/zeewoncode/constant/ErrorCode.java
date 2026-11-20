package com.zeewoncode.constant;

import lombok.Getter;

/**
 * 统一错误码
 * 200 成功；400/401/403/404/500 通用；
 * 1xxx 用户；2xxx 商品；3xxx 交易；4xxx 优惠券；5xxx 拼团；6xxx 评价
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "ok"),

    // ==================== 通用 ====================
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),

    // ==================== 用户账号 10xx ====================
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_FROZEN(1004, "用户被冻结"),
    CODE_ERROR(1005, "验证码错误"),
    USER_UPDATE_FAILED(1006, "用户信息更新失败"),
    USER_PASSWORD_UPDATE_FAILED(1007, "用户密码更新失败"),
    USER_REGISTER_FAILED(1008, "用户注册失败"),
    PHONE_ERROR(1009, "手机号格式错误"),
    NICKNAME_NOT_NULL(1010, "昵称不能为空"),
    NICKNAME_ERROR(1011, "昵称格式错误"),
    AVATAR_ERROR(1012, "头像格式错误"),
    LOGIN_FAILED(1013, "登录失败"),
    UPLOAD_FAILED(1014, "文件上传失败"),
    PASSWORD_EDIT_FAILED(1015, "密码修改失败"),

    // ==================== 收货地址 11xx ====================
    USER_ADDRESS_NOT_EXIST(1101, "用户地址不存在"),
    USER_ADDRESS_ADD_FAILED(1102, "用户地址添加失败"),
    USER_ADDRESS_UPDATE_FAILED(1103, "用户地址更新失败"),
    USER_ADDRESS_DELETE_FAILED(1104, "用户地址删除失败"),
    USER_ADDRESS_ISDEFAULT_FAILED(1105, "用户地址设为默认失败"),
    USER_ADDRESS_ISNOTDEFAULT_FAILED(1106, "用户地址设为非默认失败"),
    USER_ADDRESS_DEFAULT_CANNOT_DELETE(1107, "用户默认地址不可删除"),
    USER_ADDRESS_ISDEFAULT_ALREADY(1108, "用户地址已设为默认"),

    // ==================== 身份与权限 12xx ====================
    CURRENT_USER_IDENTITY_NOT_USER(1201, "当前用户身份不是买家"),
    CURRENT_USER_IDENTITY_NOT_BUSINESS(1202, "当前用户身份不是商家"),
    CURRENT_USER_NOT_PERMISSION(1203, "当前用户没有权限"),

    // ==================== SPU 20xx ====================
    SPU_NOT_EXIST(2001, "商品不存在"),
    SPU_NOT_SALE(2002, "商品已下架"),
    SPU_ADD_FAILED(2003, "新建商品失败"),
    SPU_DETAIL_NOT_EXIST(2004, "商品详情不存在"),
    SPU_SAME_TITLE_ALREADY(2005, "商品标题已存在"),
    SPU_UPDATE_FAILED(2006, "商品更新失败"),
    SPU_DELETE_FAILED(2007, "商品删除失败"),
    SPU_SAME_STATUS_ALREADY(2008, "商品当前状态已为该状态"),
    SPU_INFO_ERROR(2009, "商品信息错误"),
    SPU_NOT_BELONG_TO_MERCHANT(2010, "商品不属于该商家"),

    // ==================== SKU 21xx ====================
    SKU_NOT_EXIST(2101, "商品规格不存在"),
    SKU_NOT_ENOUGH(2102, "商品规格库存不足"),
    SKU_IS_NOT_SALE(2103, "商品规格已下架"),
    SKU_ADD_FAILED(2104, "商品规格新建失败"),
    SKU_STOCK_RELEASE_FAILED(2105, "商品规格预占库存释放失败"),
    SKU_SAME_SPECS_ALREADY(2106, "商品规格已存在"),
    SKU_PRICE_ERROR(2107, "商品规格价格错误"),
    SKU_UPDATE_FAILED(2108, "商品规格更新失败"),
    SKU_IS_SALE(2109, "商品规格已上架"),
    SKU_DELETE_FAILED(2110, "商品规格删除失败"),
    SKU_INFO_ERROR(2111, "商品规格信息错误"),

    // ==================== 分类 22xx ====================
    CATEGORY_NOT_EXIST(2201, "分类不存在"),
    CATEGORY_CREATE_FAILED(2202, "分类创建失败"),
    CATEGORY_UPDATE_FAILED(2203, "分类修改失败"),
    CATEGORY_HAVE_SON(2204, "分类下还有子类"),
    CATEGORY_HAVE_SPU(2205, "分类下还有商品"),
    CATEGORY_DELETE_FAILED(2206, "分类删除失败"),

    // ==================== 店铺 23xx ====================
    MERCHANT_NOT_EXIST(2301, "店铺信息不存在"),
    MERCHANT_NAME_EXIST_ALREADY(2302, "店铺名称已存在"),
    MERCHANT_CREATE_FAILED(2303, "店铺创建失败"),
    MERCHANT_UPDATE_FAILED(2304, "店铺修改信息失败"),

    // ==================== 购物车 30xx ====================
    CART_SKU_QUANTITY_UPDATE_FAILED(3001, "购物车商品规格数量更新失败"),
    CART_SKU_ADD_FAILED(3002, "购物车商品规格添加失败"),
    CART_SKU_SELECT_FAILED(3003, "购物车商品规格选择状态更新失败"),
    CART_SKU_DELETE_FAILED(3004, "购物车商品规格删除失败"),

    // ==================== 订单 31xx ====================
    ORDER_NOT_EXIST(3101, "订单不存在"),
    ORDER_STATUS_NOT_ALLOW_ADD_REVIEW(3102, "订单状态不允许添加评价"),
    ORDER_CREATE_FAILED(3103, "订单创建失败"),
    ORDER_ITEMS_CREATE_FAILED(3104, "订单商品明细项创建失败"),
    ORDER_NOT_CANCEL(3105, "订单不能取消"),
    ORDER_CANCEL_FAILED(3106, "订单取消失败"),
    ORDER_ITEMS_DELETE_FAILED(3107, "订单商品明细项删除失败"),
    ORDER_PAY_TIME_OUT(3108, "订单支付超时"),
    ORDER_STATUS_UPDATE_FAILED(3109, "订单状态修改失败"),
    ORDER_UPDATE_FAILED(3110, "订单修改失败"),
    ORDER_STATUS_ERROR(3111, "订单状态错误"),

    // ==================== 物流 32xx ====================
    SHIPMENT_NOT_EXIST(3201, "物流记录不存在"),
    SHIPMENT_UPDATE_FAILED(3202, "物流记录更新失败"),
    SHIPMENT_ADD_FAILED(3203, "物流记录添加失败"),

    // ==================== 支付 33xx ====================
    PAYMENT_CREATE_FAILED(3301, "支付记录创建失败"),

    // ==================== 优惠券 40xx ====================
    COUPON_NOT_EXIST(4001, "优惠券不存在"),
    COUPON_NOT_ENOUGH(4002, "优惠券数量不足"),
    COUPON_CONDITION_NOT_MEET(4003, "优惠券条件不满足"),
    COUPON_UPDATE_FAILED(4004, "优惠券更新失败"),
    COUPON_USED(4005, "优惠券已使用"),
    COUPON_NOT_USED(4006, "优惠券未使用"),
    COUPON_EXPIRED(4007, "优惠券已过期"),
    COUPON_USE_FAILED(4008, "优惠券使用失败"),
    COUPON_RETURN_FAILED(4009, "优惠券退还失败"),
    COUPON_CLAIMED(4010, "优惠券已领取过"),
    COUPON_STOCK_UPDATE_FAILED(4011, "优惠券数量更新失败"),
    USER_COUPON_CREATE_FAILED(4012, "用户优惠券领取记录创建失败"),
    COUPON_CONDITION_AMOUNT_ERROR(4013, "优惠券满减金额异常"),
    COUPON_DISCOUNT_AMOUNT_ERROR(4014, "优惠券优惠金额异常"),
    COUPON_DISCOUNT_RATE_ERROR(4015, "优惠券折扣异常"),
    COUPON_VALID_ERROR(4016, "优惠券过期时间异常"),
    COUPON_CREATE_FAILED(4017, "优惠券创建失败"),

    // ==================== 拼团 50xx ====================
    GROUPON_INSTANCE_NOT_EXIST(5001, "拼团事务不存在"),
    GROUPON_INSTANCE_STATUS_FALSE(5002, "拼团事务状态错误"),
    GROUPON_INSTANCE_SUCCESS(5003, "拼团事务已成功"),
    GROUPON_INSTANCE_EXPIRED(5004, "拼团事务已过期"),
    GROUPON_INSTANCE_CREATE_FAILED(5005, "拼团事务创建失败"),
    GROUPON_INSTANCE_COUNT_REDUCE_FAILED(5006, "拼团事务拼团人数减少失败"),
    GROUPON_DEFINE_NOT_EXIST(5007, "拼团活动不存在"),
    GROUPON_DEFINE_NOT_START(5008, "拼团活动还未开启"),
    GROUPON_DEFINE_END(5009, "拼团活动已结束"),
    GROUPON_INSTANCE_USING(5010, "您已处于拼团中"),
    GROUPON_INSTANCE_COUNT_ENOUGH(5011, "拼团人数已满"),
    GROUPON_INSTANCE_STATUS_UPDATE_FAILED(5012, "拼团事务状态修改失败"),
    GROUPON_PRICE_ERROR(5013, "拼团价格错误"),
    GROUPON_DEFINE_TIME_ERROR(5014, "拼团活动时间错误"),
    GROUPON_DEFINE_HOURS_ERROR(5015, "成团时间错误"),
    GROUPON_DEFINE_CREATE_FAILED(5016, "拼团活动创建失败"),
    GROUPON_DEFINE_STATUS_UPDATE_FAILED(5017, "拼团活动状态修改失败"),

    // ==================== 评价 60xx ====================
    REVIEW_ADD_FAILED(6001, "评价添加失败"),
    REVIEW_DELETE_FAILED(6002, "评价删除失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}