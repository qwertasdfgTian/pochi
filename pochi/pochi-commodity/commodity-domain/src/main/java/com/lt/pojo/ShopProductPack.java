package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 套装表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_product_pack")
public class ShopProductPack implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编号
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 套装编码，同一编码下为同一套装的商品
     */
    @TableField(value = "pack_code")
    private Long packCode;

    /**
     * 售价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 预警库存
     */
    @TableField(value = "low_stock")
    private Integer lowStock;

    /**
     * 规格名称
     */
    @TableField(value = "spec_name")
    private String specName;

    /**
     * 销量
     */
    @TableField(value = "sale")
    private Integer sale;

    /**
     * 商品名称
     */
    @TableField(value = "product_name")
    private String productName;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PRODUCT_ID = "product_id";

    public static final String COL_PACK_CODE = "pack_code";

    public static final String COL_PRICE = "price";

    public static final String COL_STOCK = "stock";

    public static final String COL_LOW_STOCK = "low_stock";

    public static final String COL_SPEC_NAME = "spec_name";

    public static final String COL_SALE = "sale";

    public static final String COL_PRODUCT_NAME = "product_name";
}