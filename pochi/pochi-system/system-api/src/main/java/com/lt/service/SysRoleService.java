package com.lt.service;

import com.lt.pojo.SysRole;
import com.lt.vo.Page;
import com.lt.vo.SysRoleVo;

import java.util.List;

public interface SysRoleService {

    /**
     * 添加
     * @param sysRole
     */
    void save(SysRoleVo sysRole);

    /**
     * 修改
     * @param sysRole
     */
    void update(SysRoleVo sysRole);

    /**
     * 根据id删除
     * @param id
     */
    void delete(Long id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    SysRoleVo get(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<SysRole> getByPage(Page<SysRole> page);

    /**
     * 查询所有角色
    */
    List<SysRole> getAll();

    /**
     * 根据角色名查询
     * @Param: roleName
    */
    SysRole getByRoleName(String roleName);
}
