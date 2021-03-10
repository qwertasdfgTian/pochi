package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 品牌分类表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_brand_category")
public class ShopBrandCategory implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 品牌id
     */
    @TableField(value = "brand_id")
    private Long brandId;

    /**
     * 分类id
     */
    @TableField(value = "category_id")
    private Long categoryId;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_BRAND_ID = "brand_id";

    public static final String COL_CATEGORY_ID = "category_id";
}