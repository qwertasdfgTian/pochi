package com.lt.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色视图类
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/16 21:07
 * @Version 1.0
 */
@Data
public class SysRoleVo implements Serializable {

    /**
     * 角色ID，自增
     */
    private Long roleId;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    /**
     * 排序值，越小越靠前
     */
    @NotNull(message = "排序值不能为空")
    private Integer roleSort;

    /**
     * 菜单ID集合
     */
    private List<Long> authIds;

}
