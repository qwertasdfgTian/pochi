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
    * 品牌表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_brand")
public class ShopBrand implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 品牌名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 显示，1是0否
     */
    @TableField(value = "show_status")
    private Integer showStatus;

    /**
     * 品牌logo
     */
    @TableField(value = "logo")
    private String logo;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_SORT = "sort";

    public static final String COL_SHOW_STATUS = "show_status";

    public static final String COL_LOGO = "logo";
}