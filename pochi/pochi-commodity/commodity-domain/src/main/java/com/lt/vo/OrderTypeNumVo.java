package com.lt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-02-23 17:20
 **/
@Data
public class OrderTypeNumVo implements Serializable {
    /**
     * 代付款数量
    */
    private Integer waitpaynum;
    /**
     * 代发货数量
    */
    private Integer waitsendnum;
    /**
     * 代收货数量
    */
    private Integer alreadysendnum;
    /**
     * 待评价数量
    */
    private Integer alreadysignnum;

    public OrderTypeNumVo(Integer waitpaynum, Integer waitsendnum, Integer alreadysendnum, Integer alreadysignnum) {
        this.waitpaynum = waitpaynum;
        this.waitsendnum = waitsendnum;
        this.alreadysendnum = alreadysendnum;
        this.alreadysignnum = alreadysignnum;
    }
}
