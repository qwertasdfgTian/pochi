package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:07
 * @Version 1.0
 */
@Data
public class ShopOrderCommentVo implements Serializable {

    private Long id;

    private Long productId;

    private Long orderId;

    private Long orderItemId;

    private Long commentId;

    private Integer commentType;

    private String nickName;

    private String productName;

    private Integer star;

    private String createTime;

    private String content;

    private List<String> picList;

    private String memberIcon;

}
