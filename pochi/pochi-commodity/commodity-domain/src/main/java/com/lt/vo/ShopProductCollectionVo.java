package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/20 14:45
 * @Version 1.0
 */
@Data
public class ShopProductCollectionVo implements Serializable {

    private Long id;

    private Long productId;

    private String productPic;

    private String productName;

    private String productBrand;

    private BigDecimal productPrice;

    private Long productCategoryId;

    private String createTime;

    private String createBy;

    /**
     * 收藏数
     */
    private Integer collectionCount;

}
