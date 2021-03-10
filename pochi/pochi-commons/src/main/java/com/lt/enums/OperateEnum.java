package com.lt.enums;

import lombok.Getter;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/20 20:57
 * @Version 1.0
 */
@Getter
public enum  OperateEnum {
    /**
     * 操作用户
     */
    USER("用户"),
    SYSTEM("系统"),
    ADMIN("管理员")
    ;

    private String type;

    OperateEnum(String type) {
        this.type = type;
    }
}
