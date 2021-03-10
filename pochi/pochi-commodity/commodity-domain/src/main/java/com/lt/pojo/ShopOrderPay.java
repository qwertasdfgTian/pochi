package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 支付订单表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_order_pay")
public class ShopOrderPay implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户账号
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 支付宝支付订单号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 对应商品订单号
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 创建支付订单的参数，json格式
     */
    @TableField(value = "order_param")
    private String orderParam;

    /**
     * 支付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 状态，0支付中，1支付成功，-1支付失败
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 我方支付订单号
     */
    @TableField(value = "out_trade_no")
    private String outTradeNo;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_ORDER_NO = "order_no";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_ORDER_PARAM = "order_param";

    public static final String COL_PAY_AMOUNT = "pay_amount";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_STATUS = "status";

    public static final String COL_DELETED = "deleted";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_OUT_TRADE_NO = "out_trade_no";
}