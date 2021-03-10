package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
    * 优惠券使用、领取历史表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_coupon_history")
public class ShopCouponHistory implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券编号
     */
    @TableField(value = "coupon_id")
    private Long couponId;

    /**
     * 领取人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 使用状态：0未使用；1已使用；2已过期
     */
    @TableField(value = "use_status")
    private Integer useStatus;

    /**
     * 使用时间
     */
    @TableField(value = "use_time")
    private String useTime;

    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private Long orderId;

    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 最少使用金额
     */
    @TableField(value = "min_point")
    private BigDecimal minPoint;

    @TableField(value = "create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_COUPON_ID = "coupon_id";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_USE_STATUS = "use_status";

    public static final String COL_USE_TIME = "use_time";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_AMOUNT = "amount";

    public static final String COL_MIN_POINT = "min_point";

    public static final String COL_CREATE_TIME = "create_time";
}