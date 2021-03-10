package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopProduct;

import java.util.List;

public interface ShopCouponMapper extends BaseMapper<ShopCoupon> {

    /**
     * 根据id查询
     * @Param: productId
    */
    ShopProduct getInfoById(Long productId);

    /**
     * 查询全场通用的优惠券
     */
    List<ShopCoupon> getBothCoupon();

    /**
     * 查询该分类的优惠券
     * @Param: categoryId
     */
    List<ShopCoupon> getByCategoryId(Long categoryId);

    /**
     * 查询该商品的优惠券
     * @Param: productId
     */
    List<ShopCoupon> getByProductId(Long productId);
}