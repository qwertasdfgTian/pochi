package com.lt.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:07
 * @Version 1.0
 */
@Data
@Document(collection = "shop_order_comment")
public class ShopOrderComment implements Serializable {

    @Id
    @Field("id")
    private Long id;

    @Field("product_id")
    private Long productId;

    @Field("order_id")
    private Long orderId;

    @Field("order_item_id")
    private Long orderItemId;

    @Field("comment_id")
    private Long commentId;

    @Field("comment_type")
    private Integer commentType;

    @Field("nick_name")
    private String nickName;

    @Field("product_name")
    private String productName;

    @Field("star")
    private Integer star;

    @Field("create_time")
    private String createTime;

    @Field("content")
    private String content;

    @Field("pics")
    private String pics;

    @Field("member_icon")
    private String memberIcon;

}
