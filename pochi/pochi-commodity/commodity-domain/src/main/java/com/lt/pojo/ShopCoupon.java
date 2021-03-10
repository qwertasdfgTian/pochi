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
    * 优惠卷表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_coupon")
public class ShopCoupon implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 使用类型：0全场通用；1指定分类；2指定商品
     */
    @TableField(value = "coupon_type")
    private Integer couponType;

    /**
     * 优惠券名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 订单金额达到多少时才可以使用
     */
    @TableField(value = "min_point")
    private BigDecimal minPoint;

    /**
     * 有效时间起
     */
    @TableField(value = "start_time")
    private String startTime;

    /**
     * 有效时间止
     */
    @TableField(value = "end_time")
    private String endTime;

    /**
     * 剩余数量
     */
    @TableField(value = "rest_count")
    private Integer restCount;

    /**
     * 发行数量
     */
    @TableField(value = "publish_count")
    private Integer publishCount;

    /**
     * 已使用数量
     */
    @TableField(value = "use_count")
    private Integer useCount;

    /**
     * 领取数量
     */
    @TableField(value = "receive_count")
    private Integer receiveCount;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

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

    /**
     * 状态，1正常，0已过期
     */
    @TableField(value = "status")
    private Integer status;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_COUPON_TYPE = "coupon_type";

    public static final String COL_NAME = "name";

    public static final String COL_AMOUNT = "amount";

    public static final String COL_MIN_POINT = "min_point";

    public static final String COL_START_TIME = "start_time";

    public static final String COL_END_TIME = "end_time";

    public static final String COL_REST_COUNT = "rest_count";

    public static final String COL_PUBLISH_COUNT = "publish_count";

    public static final String COL_USE_COUNT = "use_count";

    public static final String COL_RECEIVE_COUNT = "receive_count";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_BY = "update_by";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";

    public static final String COL_STATUS = "status";
}