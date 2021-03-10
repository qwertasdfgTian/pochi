package com.lt.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:12
 * @Version 1.0
 */
@Data
public class ShopOrderCommentDto implements Serializable {

    private Long orderItemId;

    private Long orderId;

    private Long commentId;

    private Integer commentType;

    private Integer star;

    private String content;

    private List<String> picList;

}
