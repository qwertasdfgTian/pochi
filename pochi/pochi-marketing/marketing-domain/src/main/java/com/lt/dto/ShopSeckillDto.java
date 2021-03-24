package com.lt.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ShopSeckillDto implements Serializable {
    /**
     * 秒杀活动的id
     */
    private Long id;

    /**
     * 秒杀活动的名称
     */
    @NotBlank(message = "秒杀活动的名称不能是空")
    private String name;

    /**
     * 商品的id
     */
    @NotNull(message = "秒杀商品的ID不能是空")
    private Long productId;

    /**
     * 秒杀商品的图片
     */
    @NotBlank(message = "秒杀商品的图片不能是空")
    private String productPic;

    /**
     * 秒杀商品的名称
     */
    @NotBlank(message = "秒杀商品的名称不能是空")
    private String productName;

    /**
     * 秒杀商品品牌
     */
    @NotBlank(message = "秒杀商品的品牌名称不能是空")
    private String brandName;

    /**
     * 秒杀商品的分类Id
     */
    @NotNull(message = "秒杀商品的分类ID不能是空")
    private Long categoryId;

    /**
     * 秒杀开始时间
     */
    @NotBlank(message = "秒杀开始的时间不能是空")
    private String beginTime;

    /**
     * 秒杀结束时间
     */
    @NotBlank(message = "秒杀结束的时间不能是空")
    private String endTime;

    /**
     * 秒杀价格
     */
    @NotNull(message = "秒杀活动的价格不能是空")
    private BigDecimal productPrice;

    /**
     * 商品原价
     */
    private BigDecimal productOldPrice;

    /**
     * 秒杀商品库存
     */
    @NotNull(message = "秒杀商品库存不能是空")
    private Integer stock;

    /**
     * 秒杀取消时间(单位分钟)
     */
    @NotBlank(message = "秒杀取消时间不能是空")
    private String cancelTime;

    /**
     * 秒杀活动状态(0未开始，1进行中，2已结束)
     */
    private Integer status;

}