package com.lt.enums;

import lombok.Getter;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/17 20:53
 * @Version 1.0
 */
@Getter
public enum SortTypeEnum {
    /**
     * 正序
     */
    ASC(1),
    /**
     * 倒序
     */
    DESC(2);

    private Integer type;

    SortTypeEnum(Integer type) {
        this.type = type;
    }
}
