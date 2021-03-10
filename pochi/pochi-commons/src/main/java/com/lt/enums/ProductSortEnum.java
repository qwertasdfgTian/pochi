package com.lt.enums;

import lombok.Getter;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/17 20:50
 * @Version 1.0
 */
@Getter
public enum ProductSortEnum {
    /**
     * 默认综合排序
     */
    DEFAULT(1, null),
    /**
     * 销量排序
     */
    SALE(2, "sale"),
    /**
     * 价格排序
     */
    PRICE(3, "price");

    /**
     * 排序类型
     */
    private Integer type;
    /**
     * 排序列
     */
    private String column;

    ProductSortEnum(Integer type, String column) {
        this.type = type;
        this.column = column;
    }

    /**
     * 根据type找column
     *
     * @param type
     * @return
     */
    public static String getSort(Integer type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case 2:
                return SALE.getColumn();
            case 3:
                return PRICE.getColumn();
            default:
                return DEFAULT.getColumn();
        }
    }

}
