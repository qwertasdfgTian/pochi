package com.lt.vo;

import com.lt.pojo.ShopOrderItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 20:41
 * @Version 1.0
 */
@Data
public class ShopOrderVo implements Serializable {

    /**
     * 订单项列表
     */
    private List<ShopOrderItem> itemList;

    /**
     * 订单号
     */
    private Long id;
    /**
     * 创建人手机号
     */
    private String createBy;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 应付金额
     */
    private BigDecimal payAmount;
    /**
     * 运费金额
     */
    private BigDecimal freightAmount;
    /**
     * 优惠券抵扣金额
     */
    private BigDecimal couponAmount;
    /**
     * 订单状态：0待付款；1待确认；2待发货；3已发货（待签收）；4已签收（待评价）；5已完成；6无效订单，7已关
     */
    private Integer status;
    /**
     * 订单类型，0正常订单，1秒杀订单
     */
    private Integer orderType;
    /**
     * 物流公司
     */
    private String deliveryCompany;
    /**
     * 物流单号
     */
    private String deliverySn;
    /**
     * autoConfirmDay
     */
    private Integer autoConfirmDay;
    /**
     * 可获得的积分
     */
    private String integration;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 收货人邮编
     */
    private String receiverPostCode;
    /**
     * 省份
     */
    private String receiverProvince;
    /**
     * 城市
     */
    private String receiverCity;
    /**
     * 区县
     */
    private String receiverRegion;
    /**
     * 详细地址
     */
    private String receiverDetailAddress;
    /**
     * 订单备注
     */
    private String note;
    /**
     * 确认收货状态，0未确认，1已确认
     */
    private Integer confirmStatus;
    /**
     * 支付时间
     */
    private String paymentTime;
    /**
     * 发货时间
     */
    private String deliveryTime;
    /**
     * 确认收货时间
     */
    private String receiveTime;
    /**
     * 评价时间
     */
    private String commentTime;
    /**
     * 是否评论，1是0否
     */
    private Integer isComment;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改时间
     */
    private String updateTime;

}
