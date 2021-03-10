package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
    * 产品分类
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_product_category")
public class ShopProductCategory implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级分类，0表示顶级分类
     */
    @TableField(value = "parent_id")
    @NotNull(message = "上级分类不能为空")
    private Long parentId;

    /**
     * 分类名称
     */
    @TableField(value = "name")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类级别：1级，2级，3级。暂时只支持3级分类
     */
    @TableField(value = "level")
    @NotNull(message = "分类级别不能为空")
    private Integer level;

    /**
     * 是否显示在导航栏：1是0否
     */
    @TableField(value = "nav_status")
    private Integer navStatus;

    @TableField(value = "sort")
    @NotNull(message = "排序编码不能为空")
    private Integer sort;

    /**
     * 图标
     */
    @TableField(value = "icon")
    private String icon;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_NAME = "name";

    public static final String COL_LEVEL = "level";

    public static final String COL_NAV_STATUS = "nav_status";

    public static final String COL_SORT = "sort";

    public static final String COL_ICON = "icon";
}