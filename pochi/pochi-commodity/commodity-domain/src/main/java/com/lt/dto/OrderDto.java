package com.lt.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/20 20:43
 * @Version 1.0
 */
@Data
public class OrderDto implements Serializable {

    /**
     * 商品列表
     */
    private List<OrderProductDto> productList;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 备注
     */
    private String note;

}
