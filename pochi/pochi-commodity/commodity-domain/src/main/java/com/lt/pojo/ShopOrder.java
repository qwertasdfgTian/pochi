package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 订单表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_order")
public class ShopOrder implements Serializable {
    /**
     * 订单号
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户帐号
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 订单总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 应付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 运费金额
     */
    @TableField(value = "freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠券抵扣金额
     */
    @TableField(value = "coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 订单状态：0待付款；1待发货；2已发货（待签收）；3已签收（待评价）；4已完成；5无效订单，6已关闭
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 订单类型：0正常订单；1秒杀订单
     */
    @TableField(value = "order_type")
    private Integer orderType;

    /**
     * 物流公司(配送方式)
     */
    @TableField(value = "delivery_company")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @TableField(value = "delivery_sn")
    private String deliverySn;

    /**
     * 自动确认时间（天）
     */
    @TableField(value = "auto_confirm_day")
    private Integer autoConfirmDay;

    /**
     * 可以获得的积分
     */
    @TableField(value = "integration")
    private String integration;

    /**
     * 收货人姓名
     */
    @TableField(value = "receiver_name")
    private String receiverName;

    /**
     * 收货人电话
     */
    @TableField(value = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货人邮编
     */
    @TableField(value = "receiver_post_code")
    private String receiverPostCode;

    /**
     * 省份/直辖市
     */
    @TableField(value = "receiver_province")
    private String receiverProvince;

    /**
     * 城市
     */
    @TableField(value = "receiver_city")
    private String receiverCity;

    /**
     * 区
     */
    @TableField(value = "receiver_region")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @TableField(value = "receiver_detail_address")
    private String receiverDetailAddress;

    /**
     * 订单备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 确认收货状态：0未确认；1已确认
     */
    @TableField(value = "confirm_status")
    private Integer confirmStatus;

    /**
     * 支付时间
     */
    @TableField(value = "payment_time")
    private String paymentTime;

    /**
     * 发货时间
     */
    @TableField(value = "delivery_time")
    private String deliveryTime;

    /**
     * 确认收货时间
     */
    @TableField(value = "receive_time")
    private String receiveTime;

    /**
     * 评价时间
     */
    @TableField(value = "comment_time")
    private String commentTime;

    /**
     * 是否评论，1是0否
     */
    @TableField(value = "is_comment")
    private Integer isComment;

    /**
     * 提交时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(exist = false)
    private String openId;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_TOTAL_AMOUNT = "total_amount";

    public static final String COL_PAY_AMOUNT = "pay_amount";

    public static final String COL_FREIGHT_AMOUNT = "freight_amount";

    public static final String COL_COUPON_AMOUNT = "coupon_amount";

    public static final String COL_STATUS = "status";

    public static final String COL_ORDER_TYPE = "order_type";

    public static final String COL_DELIVERY_COMPANY = "delivery_company";

    public static final String COL_DELIVERY_SN = "delivery_sn";

    public static final String COL_AUTO_CONFIRM_DAY = "auto_confirm_day";

    public static final String COL_INTEGRATION = "integration";

    public static final String COL_RECEIVER_NAME = "receiver_name";

    public static final String COL_RECEIVER_PHONE = "receiver_phone";

    public static final String COL_RECEIVER_POST_CODE = "receiver_post_code";

    public static final String COL_RECEIVER_PROVINCE = "receiver_province";

    public static final String COL_RECEIVER_CITY = "receiver_city";

    public static final String COL_RECEIVER_REGION = "receiver_region";

    public static final String COL_RECEIVER_DETAIL_ADDRESS = "receiver_detail_address";

    public static final String COL_NOTE = "note";

    public static final String COL_CONFIRM_STATUS = "confirm_status";

    public static final String COL_PAYMENT_TIME = "payment_time";

    public static final String COL_DELIVERY_TIME = "delivery_time";

    public static final String COL_RECEIVE_TIME = "receive_time";

    public static final String COL_COMMENT_TIME = "comment_time";

    public static final String COL_IS_COMMENT = "is_comment";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";
}