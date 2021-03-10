package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_pack")
public class ShopPack implements Serializable {
    /**
     * 套装ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 套装名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 商品数
     */
    @TableField(value = "product_count")
    private Integer productCount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_PRODUCT_COUNT = "product_count";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_CREATE_BY = "create_by";
}