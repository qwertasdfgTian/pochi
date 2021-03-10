package com.lt.service;

import com.lt.dto.SysUserDto;
import com.lt.pojo.SysUser;
import com.lt.vo.Page;

public interface SysUserService {

    /**
     *更新指定用户名的登陆时间
     * @Param: username
    */
    void updateLoginTime(String username);

    /**
     *根据用户名查询用户
     * @Param: username
    */
    SysUser getByUsername(String username);

    /**
     * 保存用户
     * @Param: sysUser
    */
    void save(SysUserDto sysUserDto);

    /**
     * 更新用户
     * @Param: sysUser
    */
    void update(SysUserDto sysUserDto);

    /**
     * 删除用户
     * @Param: id
    */
    void delete(Long id);

    /**
     * 启用用户
     * @Param: id
    */
    void enable(Long id);

    /**
     * 封禁用户
     * @Param: id
    */
    void disable(Long id);

    /**
     * 分页查询
     * @Param: page
    */
    Page<SysUser> getByPage(Page<SysUser> page);

    /**
     * 根据id查询用户
     * @Param: id
    */
    SysUserDto get(Long id);
}
