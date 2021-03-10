package com.lt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 杨德石
 * @Date: 2021/1/26 22:16
 * @Version 1.0
 */
@Data
public class OrderMonthVo implements Serializable {

    private String day;

    private Integer count;

}
