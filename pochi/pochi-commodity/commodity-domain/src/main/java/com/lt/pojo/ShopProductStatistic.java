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
    * 商品统计信息
    */
@Data
@AllArgsConstructor
@TableName(value = "shop_product_statistic")
public class ShopProductStatistic implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编号
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 收藏数
     */
    @TableField(value = "collection_count")
    private Integer collectionCount;

    /**
     * 浏览数
     */
    @TableField(value = "history_count")
    private Integer historyCount;

    /**
     * 订单数
     */
    @TableField(value = "order_count")
    private Integer orderCount;

    /**
     * 评论数
     */
    @TableField(value = "comment_count")
    private Integer commentCount;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PRODUCT_ID = "product_id";

    public static final String COL_COLLECTION_COUNT = "collection_count";

    public static final String COL_HISTORY_COUNT = "history_count";

    public static final String COL_ORDER_COUNT = "order_count";

    public static final String COL_COMMENT_COUNT = "comment_count";

    public ShopProductStatistic() {
        this.collectionCount = 0;
        this.historyCount = 0;
        this.commentCount = 0;
        this.orderCount = 0;
    }
}