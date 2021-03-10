package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/26 23:39
 * @Version 1.0
 */
@Data
public class ShopUserStatisticVo implements Serializable {

    /**
     * 柱名
     */
    private String name;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 数量
     */
    private BigDecimal count;

}
