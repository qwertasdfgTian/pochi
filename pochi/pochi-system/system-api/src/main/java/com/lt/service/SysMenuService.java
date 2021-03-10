package com.lt.service;

import com.lt.pojo.SysMenu;
import com.lt.vo.Page;
import com.lt.vo.RouterVo;
import com.lt.vo.SysMenuVo;

import java.util.List;

public interface SysMenuService {


    /**
     * 添加菜单
     * @Param: sysMenu
    */
    void save(SysMenu sysMenu);

    /**
     * 修改菜单
     * @Param: sysMenu
    */
    void update(SysMenu sysMenu);

    /**
     * 根据id删除
     * @Param: id
    */
    void delete(Long id);

    /**
     * 根据id查询
     * @Param: id
    */
    SysMenu get(Long id);

    /**
     * 分页查询
     * @Param: page
    */
    Page<SysMenu> getByPage(Page<SysMenu> page);

    /**
     * 树形查询
    */
    List<SysMenuVo> getTreeList();

    /**
     * 根据角色id查询选中的菜单
     * @Param: roleId
    */
    List<Long> getRoleSelectMenu(Long roleId);

    /**
     * 获取动态路由
    */
    List<RouterVo> getRouters();
}
