package com.lt.dto;

import com.lt.pojo.ShopPack;
import com.lt.pojo.ShopProduct;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/28 20:36
 * @Version 1.0
 */
@Data
public class ShopProductDto implements Serializable {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 商品副标题
     */
    private String subTitle;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    private Integer stock;

    /**
     * 库存预警值
     */
    private Integer lowStock;

    /**
     * 购买积分
     */
    private BigDecimal point;

    /**
     * 轮播图片
     */
    private List<String> albumPicList;

    /**
     * 商品描述
     */
    private String productComment;

    /**
     * 详情
     */
    private String productContent;

    /**
     * 商品分类ID
     */
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    /**
     * 套装编号
     */
    private Long packCode;

    /**
     * 品牌ID
     */
    @NotNull(message = "商品品牌不能为空")
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 运费
     */
    private BigDecimal transFee;

    /**
     * 规格
     */
    @NotBlank(message = "规格不能为空")
    private String specs;

    /**
     * 是否上架
     */
    private Integer publishStatus;

    /**
     * 是否新品
     */
    private Integer newStatus;

    /**
     * 是否推荐
     */
    private Integer recommendStatus;

    /**
     * 排序
     */
    @NotNull(message = "排序编码不能为空")
    private Integer sort;

    /**
     * 促销价格
     */
    private BigDecimal promotionPrice;

    /**
     * 套装
     */
    private ShopPack shopPack;

    /**
     * 商品列表
     */
    private List<ShopProduct> productList;

}
