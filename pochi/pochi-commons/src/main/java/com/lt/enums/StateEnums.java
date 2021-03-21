package com.lt.enums;

import cn.hutool.captcha.AbstractCaptcha;
import lombok.Getter;

/**
 * 状态码枚举。所有的状态码都在这里编写
 *
 * @Author: Mr.Tian
 * @Date: 2020/2/9 14:19
 * @Version 1.0
 */
@Getter
public enum StateEnums {
    /**
     * 逻辑删除状态
     */
    DELETED(1, "已删除"),
    NOT_DELETED(0, "未删除"),

    /**
     * 启用状态
     */
    ENABLED(1, "启用"),
    NOT_ENABLE(0, "未启用"),

    /**
     * 性别状态
     */
    SEX_MAN(1, "男"),
    SEX_WOMAN(2, "女"),

    /**
     * 请求访问状态枚举
     */
    REQUEST_SUCCESS(1, "请求正常"),
    REQUEST_ERROR(0, "请求异常"),
    /**
     * 菜单状态枚举
     */
    FOLDER(1, "目录"),
    MENU(2, "菜单"),
    AUTH(3, "权限"),
    /**
     * 优惠券状态枚举
     */
    ALL(0, "全场通用"),
    CATEGORY(1, "指定分类"),
    PRODUCT(2, "指定商品"),
    /**
     * 绑定账户状态
     */
    EXISTS_USER(1, "绑定已有账户"),
    NEW_USER(2, "绑定新账户"),

    ADDRESS_DEFAULT(1, "默认"),
    ADDRESS_NOT_DEFAULT(0, "非默认"),
    /**
     * 优惠券领取状态
    */
    COUPON_NORMAL(1,"正常"),
    COUPON_TIMEOUT(0,"过期"),
    COUPON_RECEIVE(999, "已领取"),

    /**
     * 优惠券历史状态
     */
    COUPON_NOTUSED(0,"未使用"),
    COUPON_USED(1,"已使用"),
    COUPON_EXPIRED(2, "已过期"),
    /**
     * 评论状态
     */
    COMMENT_SIMPLE(1, "普通评价"),
    COMMENT_MCH(2, "商家回复"),
    COMMENT_REVIEW(3, "追评"),
    /**
     * 秒杀状态
     */
    SECKILL_NOTSTART(0,"未开始"),
    SECKILL_START(1,"已开始"),
    SECKILL_OVER(2, "已结束"),
    ;
    private Integer code;
    private String msg;

    StateEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
