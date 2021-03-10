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
    * 菜单权限表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_menu")
public class SysMenu implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单状态（1显示 0隐藏）
     */
    @TableId(value = "visible", type = IdType.INPUT)
    private Integer visible;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    @NotBlank(message = "菜单名不能为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField(value = "router_path")
    private String routerPath;

    /**
     * 组件路径
     */
    @TableField(value = "component_url")
    private String componentUrl;

    /**
     * 菜单类型（1目录 2菜单 3权限）
     */
    @TableField(value = "menu_type")
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * 菜单状态（1正常 0停用）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 权限标识
     */
    @TableField(value = "permission")
    private String permission;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public static final String COL_MENU_ID = "menu_id";

    public static final String COL_VISIBLE = "visible";

    public static final String COL_MENU_NAME = "menu_name";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_ORDER_NUM = "order_num";

    public static final String COL_ROUTER_PATH = "router_path";

    public static final String COL_COMPONENT_URL = "component_url";

    public static final String COL_MENU_TYPE = "menu_type";

    public static final String COL_STATUS = "status";

    public static final String COL_PERMISSION = "permission";

    public static final String COL_ICON = "icon";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_BY = "update_by";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";
}