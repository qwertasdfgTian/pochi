package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
    * 商品表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_product")
public class ShopProduct implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 商品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 商品图片
     */
    @TableField(value = "pic")
    private String pic;

    /**
     * 副标题
     */
    @TableField(value = "sub_title")
    private String subTitle;

    /**
     * 价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 销量
     */
    @TableField(value = "sale")
    private Integer sale;

    /**
     * 评论数
     */
    @TableField(value = "comment_num")
    private Integer commentNum;

    /**
     * 库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 库存预警值
     */
    @TableField(value = "low_stock")
    private Integer lowStock;

    /**
     * 购买积分
     */
    @TableField(value = "point")
    private BigDecimal point;

    /**
     * 轮播图图片，至少1张，英文逗号隔开
     */
    @TableField(value = "album_pics")
    private String albumPics;

    /**
     * 商品描述
     */
    @TableField(value = "product_comment")
    private String productComment;

    /**
     * 详情
     */
    @TableField(value = "product_content")
    private String productContent;

    /**
     * 商品分类id
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 套装编号
     */
    @TableField(value = "pack_code")
    private Long packCode;

    /**
     * 品牌id
     */
    @TableField(value = "brand_id")
    private Long brandId;

    /**
     * 品牌名称
     */
    @TableField(value = "brand_name")
    private String brandName;

    /**
     * 运费
     */
    @TableField(value = "trans_fee")
    private Long transFee;

    /**
     * 规格
     */
    @TableField(value = "specs")
    private String specs;

    /**
     * 是否上架，1是0否
     */
    @TableField(value = "publish_status")
    private Integer publishStatus;

    /**
     * 是否新品，1是0否
     */
    @TableField(value = "new_status")
    private Integer newStatus;

    /**
     * 是否推荐，1是0否
     */
    @TableField(value = "recommend_status")
    private Integer recommendStatus;

    /**
     * 是否审核，1通过，0未审核，-1不通过
     */
    @TableField(value = "verify_status")
    private Integer verifyStatus;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 促销价格
     */
    @TableField(value = "promotion_price")
    private BigDecimal promotionPrice;

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

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_PIC = "pic";

    public static final String COL_SUB_TITLE = "sub_title";

    public static final String COL_PRICE = "price";

    public static final String COL_SALE = "sale";

    public static final String COL_COMMENT_NUM = "comment_num";

    public static final String COL_STOCK = "stock";

    public static final String COL_LOW_STOCK = "low_stock";

    public static final String COL_POINT = "point";

    public static final String COL_ALBUM_PICS = "album_pics";

    public static final String COL_PRODUCT_COMMENT = "product_comment";

    public static final String COL_PRODUCT_CONTENT = "product_content";

    public static final String COL_CATEGORY_ID = "category_id";

    public static final String COL_PACK_CODE = "pack_code";

    public static final String COL_BRAND_ID = "brand_id";

    public static final String COL_BRAND_NAME = "brand_name";

    public static final String COL_TRANS_FEE = "trans_fee";

    public static final String COL_SPECS = "specs";

    public static final String COL_PUBLISH_STATUS = "publish_status";

    public static final String COL_NEW_STATUS = "new_status";

    public static final String COL_RECOMMEND_STATUS = "recommend_status";

    public static final String COL_VERIFY_STATUS = "verify_status";

    public static final String COL_SORT = "sort";

    public static final String COL_PROMOTION_PRICE = "promotion_price";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_UPDATE_BY = "update_by";
}