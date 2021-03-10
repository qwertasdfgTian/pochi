package com.lt.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/17 20:48
 * @Version 1.0
 */
@Data
public class ShopProductSearchDto implements Serializable {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 排序标识
     * 1综合排序，2销量排序，3价格排序
     */
    private Integer sorted;

    /**
     * 排序方式
     * 1正序，2倒序
     */
    private Integer sortType;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 价格左区间
     */
    private BigDecimal priceStart;

    /**
     * 价格右区间
     */
    private BigDecimal priceEnd;

    private Integer pageNumber;
    private Integer pageSize;

}
