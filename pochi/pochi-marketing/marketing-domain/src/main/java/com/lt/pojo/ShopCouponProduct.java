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
    * 优惠券-商品表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_coupon_product")
public class ShopCouponProduct implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券ID
     */
    @TableField(value = "coupon_id")
    private Long couponId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_COUPON_ID = "coupon_id";

    public static final String COL_PRODUCT_ID = "product_id";

    public ShopCouponProduct(long couponId, Long id) {
        this.couponId=couponId;
        this.productId=id;
    }
}