package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-02-23 17:53
 **/
@Data
public class MyWalletVo implements Serializable {

    /**
     * 用户积分
    */
    private BigDecimal myPoint;

    /**
     * 优惠券数量
    */
    private Integer couponCount;
}
