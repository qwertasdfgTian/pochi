package com.lt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 杨德石
 * @Date: 2021/1/26 22:24
 * @Version 1.0
 */
@Data
public class OrderPointVo implements Serializable {

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 中文状态
     */
    private String statusMsg;

    /**
     * 订单数
     */
    private Integer count;

}
