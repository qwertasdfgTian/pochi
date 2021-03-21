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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_seckill")
public class ShopSeckill implements Serializable {
    /**
     * 秒杀活动的id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 秒杀活动的名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 秒杀商品的图片
     */
    @TableField(value = "product_pic")
    private String productPic;

    /**
     * 秒杀商品的名称
     */
    @TableField(value = "product_name")
    private String productName;

    /**
     * 秒杀开始时间
     */
    @TableField(value = "begin_time")
    private String beginTime;

    /**
     * 秒杀结束时间
     */
    @TableField(value = "end_time")
    private String endTime;

    /**
     * 秒杀价格
     */
    @TableField(value = "product_price")
    private BigDecimal productPrice;

    /**
     * 商品原价
     */
    @TableField(value = "product_old_price")
    private BigDecimal productOldPrice;

    /**
     * 秒杀商品库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 秒杀取消时间(单位分钟)
     */
    @TableField(value = "cancel_time")
    private String cancelTime;

    /**
     * 秒杀活动状态(0未开始，1进行中，2已结束)
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 秒杀限额
     */
    @TableField(value = "quota")
    private Integer quota;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_PRODUCT_PIC = "product_pic";

    public static final String COL_PRODUCT_NAME = "product_name";

    public static final String COL_BEGIN_TIME = "begin_time";

    public static final String COL_END_TIME = "end_time";

    public static final String COL_PRODUCT_PRICE = "product_price";

    public static final String COL_STOCK = "stock";

    public static final String COL_CANCEL_TIME = "cancel_time";

    public static final String COL_STATUS = "status";

    public static final String COL_QUOTA = "quota";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_UPDATE_BY = "update_by";
}