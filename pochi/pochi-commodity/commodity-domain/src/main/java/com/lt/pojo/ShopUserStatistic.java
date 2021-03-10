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
    * 会员统计信息
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_user_statistic")
public class ShopUserStatistic implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 用户编号
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 累计消费金额
     */
    @TableField(value = "consume_amount")
    private BigDecimal consumeAmount;

    /**
     * 订单数量
     */
    @TableField(value = "order_count")
    private Integer orderCount;

    /**
     * 优惠券数量
     */
    @TableField(value = "coupon_count")
    private Integer couponCount;

    /**
     * 评价数
     */
    @TableField(value = "comment_count")
    private Integer commentCount;

    /**
     * 退货数量
     */
    @TableField(value = "return_order_count")
    private Integer returnOrderCount;

    /**
     * 登录次数
     */
    @TableField(value = "login_count")
    private Integer loginCount;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_CONSUME_AMOUNT = "consume_amount";

    public static final String COL_ORDER_COUNT = "order_count";

    public static final String COL_COUPON_COUNT = "coupon_count";

    public static final String COL_COMMENT_COUNT = "comment_count";

    public static final String COL_RETURN_ORDER_COUNT = "return_order_count";

    public static final String COL_LOGIN_COUNT = "login_count";
}