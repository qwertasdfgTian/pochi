package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/15 22:57
 * @Version 1.0
 */
@Data
public class SysMenuVo implements Serializable {

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单名
     */
    private String menuName;

    /**
     * 父菜单编号
     */
    private Long parentId;

    /**
     * 排序值
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String routerPath;

    /**
     * 组件路径
     */
    protected String componentUrl;

    /**
     * 菜单类型，1目录，2菜单，3权限
     */
    private Integer menuType;

    /**
     * 是否显示，1是0否
     */
    private Integer visible;

    /**
     * 是否启用，1是0否
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 子节点
     */
    private List<SysMenuVo> children;

}
