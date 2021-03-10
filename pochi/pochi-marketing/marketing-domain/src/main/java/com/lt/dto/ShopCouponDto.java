package com.lt.dto;

import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopProductCategory;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/9 20:48
 * @Version 1.0
 */
@Data
public class ShopCouponDto implements Serializable {

    /**
     * 编号，雪花算法
     */
    private Long id;

    /**
     * 使用类型，0全场通用，1指定分类，2指定商品
     */
    private Integer couponType;

    /**
     * 名称
     */
    @NotBlank(message = "请输入优惠券名称")
    private String name;

    /**
     * 优惠券金额
     */
    @NotNull(message = "请输入优惠券面值")
    private BigDecimal amount;

    /**
     * 使用门槛
     */
    @NotNull(message = "请输入优惠券使用门槛")
    private BigDecimal minPoint;

    /**
     * 有效时间起
     */
    private String startTime;

    /**
     * 有效时间止
     */
    @NotBlank(message = "请选择优惠券使用日期范围")
    private String endTime;

    /**
     * 发行数量
     */
    @NotNull(message = "请输入优惠券发行数量")
    private Integer publishCount;

    /**
     * 状态，1正常0过期
     */
    private Integer status;

    /**
     * 分类列表
     */
    private List<ShopProductCategory> categoryList;

    /**
     * 商品列表
     */
    private List<ShopProduct> productList;


}
