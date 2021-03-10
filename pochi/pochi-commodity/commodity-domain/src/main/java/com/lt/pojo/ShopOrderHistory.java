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

/**
    * 订单状态变化历史记录
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_order_history")
public class ShopOrderHistory implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 操作人：用户；系统；管理员
     */
    @TableField(value = "operate_man")
    private String operateMan;

    /**
     * 订单状态：0待付款；1待发货；2已发货（待签收）；3已签收（待评价）；4已完成；5无效订单，6已关闭
     */
    @TableField(value = "order_status")
    private Integer orderStatus;

    /**
     * 备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 操作时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_OPERATE_MAN = "operate_man";

    public static final String COL_ORDER_STATUS = "order_status";

    public static final String COL_NOTE = "note";

    public static final String COL_CREATE_TIME = "create_time";
}