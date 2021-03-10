package com.lt.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/28 20:36
 * @Version 1.0
 */
@Data
@Document(indexName = "product")
public class ShopProductEs implements Serializable {

    @Id
    private String id;

    /**
     * 商品ID
     */
    @Field(name = "product_id", type = FieldType.Long)
    private Long productId;

    /**
     * 商品名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /**
     * 商品图片
     */
    @Field(type = FieldType.Keyword)
    private String pic;

    /**
     * 价格
     */
    @Field(type = FieldType.Double)
    private BigDecimal price;

    /**
     * 销量
     */
    @Field(type = FieldType.Integer)
    private Integer sale;

    /**
     * 库存
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

    /**
     * 商品描述
     */
    @Field(name = "product_comment", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String productComment;

    /**
     * 详情
     */
    @Field(name = "product_content", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String productContent;

    /**
     * 商品分类ID
     */
    @Field(name = "category_id", type = FieldType.Long)
    private Long categoryId;

    /**
     * 品牌ID
     */
    @Field(name = "brand_id", type = FieldType.Long)
    private Long brandId;

    /**
     * 排序
     */
    @Field(type = FieldType.Integer)
    private Integer sort;

}
