package com.lt.vo;

import com.lt.pojo.ShopUserStatistic;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/24 17:37
 * @Version 1.0
 */
@Data
public class ShopUserVo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 1正常0封禁
     */
    private Integer status;

    /**
     * 头像
     */
    private String header;

    /**
     * 性别，1男2女
     */
    private Integer gender;

    /**
     * 个性签名
     */
    private String note;

    /**
     * 积分
     */
    private BigDecimal point;

    /**
     * 历史积分
     */
    private BigDecimal historyPoint;

    /**
     * 创建时间
     */
    private String createTime;

    private ShopUserStatistic shopUserStatistic;

}
