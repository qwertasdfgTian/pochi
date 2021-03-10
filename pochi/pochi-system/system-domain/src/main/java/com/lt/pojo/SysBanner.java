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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
    * 首页轮播广告表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_banner")
public class SysBanner implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    @NotBlank(message = "轮播图名不能为空")
    private String name;

    /**
     * 图片路径
     */
    @TableField(value = "pic")
    private String pic;

    /**
     * 1启用，0弃用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 点击数
     */
    @TableField(value = "click_count")
    private Integer clickCount;

    /**
     * 下单数
     */
    @TableField(value = "order_count")
    private Integer orderCount;

    /**
     * 链接地址
     */
    @TableField(value = "url")
    @NotBlank(message = "链接地址不能为空")
    private String url;

    /**
     * 备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 排序
     */
    @TableField(value = "sort")
    @NotNull(message = "排序码不能为空")
    private Integer sort;

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
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_PIC = "pic";

    public static final String COL_STATUS = "status";

    public static final String COL_CLICK_COUNT = "click_count";

    public static final String COL_ORDER_COUNT = "order_count";

    public static final String COL_URL = "url";

    public static final String COL_NOTE = "note";

    public static final String COL_SORT = "sort";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_UPDATE_BY = "update_by";

    public static final String COL_DELETED = "deleted";
}