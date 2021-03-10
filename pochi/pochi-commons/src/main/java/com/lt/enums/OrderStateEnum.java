package com.lt.enums;

import lombok.Getter;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/20 20:58
 * @Version 1.0
 */
@Getter
public enum OrderStateEnum {
    /**
     * 订单状态：0待付款；1待确认；2待发货；3已发货（待签收）；4已签收（待评价）；5已完成；6无效订单，7已关
     */
    WAIT_PAY(0, "待付款"),
    WAIT_CONFIRM(1, "待确认"),
    WAIT_SEND(2, "待发货"),
    ALREADY_SEND(3, "待签收"),
    ALREADY_SIGN(4, "待评价"),
    FINISH(5, "已完成"),
    INVALID(6, "无效订单"),
    CLOSED(7, "已关闭"),
    /**
     * 支付订单状态
     */
    /**
     * 支付中
     */
    PAYING(0, "支付中"),
    /**
     * 支付成功
     */
    PAY_SUCCESS(1, "支付成功"),
    /**
     * 支付失败
     */
    PAY_FAIL(-1, "支付失败"),
    ;

    private Integer code;
    private String msg;

    OrderStateEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static OrderStateEnum getStatusByCode(Integer code) {
        switch (code) {
            case 1:
                return WAIT_CONFIRM;
            case 2:
                return WAIT_SEND;
            case 3:
                return ALREADY_SEND;
            case 4:
                return ALREADY_SIGN;
            case 5:
                return FINISH;
            case 6:
                return INVALID;
            case 7:
                return CLOSED;
            default:
                return WAIT_PAY;
        }
    }

}
