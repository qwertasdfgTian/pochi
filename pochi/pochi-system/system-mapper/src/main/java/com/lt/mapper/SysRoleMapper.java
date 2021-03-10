package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 保存角色并且把id封装在实体类里面
     * @Param: role
    */
    void save(SysRole role);
}