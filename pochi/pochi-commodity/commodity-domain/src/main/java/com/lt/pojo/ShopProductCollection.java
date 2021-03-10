package com.lt.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/20 14:45
 * @Version 1.0
 */
@Data
@Document(collection = "shop_product_collection")
public class ShopProductCollection implements Serializable {

    @Id
    private Long id;

    @Field("product_id")
    private Long productId;

    @Field("product_pic")
    private String productPic;

    @Field("product_name")
    private String productName;

    @Field("product_brand")
    private String productBrand;

    @Field("product_price")
    private BigDecimal productPrice;

    @Field("product_category_id")
    private Long productCategoryId;

    @Field("create_time")
    private String createTime;

    @Field("create_by")
    private String createBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShopProductCollection that = (ShopProductCollection) o;
        return productId.equals(that.productId) &&
                createBy.equals(that.createBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, createBy);
    }
}
