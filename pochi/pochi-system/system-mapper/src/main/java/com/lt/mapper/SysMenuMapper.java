package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getRoleSelectMenu(Long roleId);

    List<SysMenu> getEnableMenuByUserId(Long id);

    List<String> getMenuCodeByUserId(Long userId);
}