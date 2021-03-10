package com.lt.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/20 20:43
 * @Version 1.0
 */
@Data
public class OrderProductDto implements Serializable {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品个数
     */
    private Integer count;

    /**
     * 购物车项ID
     */
    private Long cartId;

}
