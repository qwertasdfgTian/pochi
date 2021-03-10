package com.lt.vo;

import com.lt.pojo.ShopCoupon;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-02-27 20:54
 **/
@Data
public class MyBothCouponVo implements Serializable {

    // 过期优惠券列表
    private List<ShopCoupon> expiredCoupon;

    // 未使用优惠券列表
    private List<ShopCoupon> notUsedCoupon;

    // 使用的优惠券列表
    private List<ShopCoupon> usedCoupon;
}
