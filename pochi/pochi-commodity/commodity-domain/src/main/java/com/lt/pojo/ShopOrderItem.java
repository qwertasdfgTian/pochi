package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 订单项表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_order_item")
public class ShopOrderItem implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 商品编号
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 商品图片
     */
    @TableField(value = "product_pic")
    private String productPic;

    /**
     * 商品名称
     */
    @TableField(value = "product_name")
    private String productName;

    /**
     * 商品品牌
     */
    @TableField(value = "product_brand")
    private String productBrand;

    /**
     * 销售价格
     */
    @TableField(value = "product_price")
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @TableField(value = "product_quantity")
    private Integer productQuantity;

    /**
     * 商品分类id
     */
    @TableField(value = "product_category_id")
    private Long productCategoryId;

    /**
     * 积分
     */
    @TableField(value = "integration")
    private Integer integration;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_ORDER_SN = "order_sn";

    public static final String COL_PRODUCT_ID = "product_id";

    public static final String COL_PRODUCT_PIC = "product_pic";

    public static final String COL_PRODUCT_NAME = "product_name";

    public static final String COL_PRODUCT_BRAND = "product_brand";

    public static final String COL_PRODUCT_PRICE = "product_price";

    public static final String COL_PRODUCT_QUANTITY = "product_quantity";

    public static final String COL_PRODUCT_CATEGORY_ID = "product_category_id";

    public static final String COL_INTEGRATION = "integration";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";
}