package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 优惠券和产品分类关系表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_coupon_category")
public class ShopCouponCategory implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券ID
     */
    @TableField(value = "coupon_id")
    private Long couponId;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_COUPON_ID = "coupon_id";

    public static final String COL_CATEGORY_ID = "category_id";

    public ShopCouponCategory(long couponId, Long id) {
        this.couponId=couponId;
        this.categoryId=id;
    }
}