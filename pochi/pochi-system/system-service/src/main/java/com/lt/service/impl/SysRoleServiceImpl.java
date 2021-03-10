package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.mapper.SysRoleMapper;
import com.lt.mapper.SysRoleMenuMapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysRoleMenu;
import com.lt.service.SysRoleService;
import com.lt.utils.ShiroUtils;
import com.lt.pojo.SysRole;
import com.lt.utils.StringUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.SysRoleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRoleVo sysRole) {
        // 设置创建人和修改人为用户名
        LoginUser loginUser = ShiroUtils.getLoginUser();
        String username = loginUser.getUsername();
        // 创建SysRole对象
        SysRole role = new SysRole();
        // 拷贝属性
        BeanUtils.copyProperties(sysRole, role);
        role.setCreateBy(username);
        role.setUpdateBy(username);
        this.sysRoleMapper.save(role);
        // 下面开始添加角色权限数据
        saveRoleMenu(sysRole, role);
    }

    /**
     * 保存角色菜单数据
     * @param sysRole
     * @param role
     */
    private void saveRoleMenu(SysRoleVo sysRole, SysRole role) {
        if(!CollectionUtils.isEmpty(sysRole.getAuthIds())) {
            // 根据菜单ID集合去构建SysRoleMenu
            List<SysRoleMenu> roleMenuList = sysRole.getAuthIds().stream().map(id -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                // 设置菜单ID和角色ID
                sysRoleMenu.setMenuId(id);
                sysRoleMenu.setRoleId(role.getRoleId());
                return sysRoleMenu;
            }).collect(Collectors.toList());
            // 存库
            for (SysRoleMenu sysRoleMenu : roleMenuList) {
                this.sysRoleMenuMapper.insert(sysRoleMenu);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleVo sysRole) {
        // 设置更新人
        LoginUser loginUser = ShiroUtils.getLoginUser();
        String username = loginUser.getUsername();
        // 创建SysRole对象
        SysRole role = new SysRole();
        // 拷贝属性
        BeanUtils.copyProperties(sysRole, role);
        role.setUpdateBy(username);
        this.sysRoleMapper.updateById(role);
        // 下面开始添加角色权限数据
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysRoleMenu.COL_ROLE_ID,sysRole.getRoleId());
        this.sysRoleMenuMapper.delete(qw);
        saveRoleMenu(sysRole, role);
    }

    @Override
    public void delete(Long id) {
        // 创建SysRole对象
        SysRole role = new SysRole();
        role.setRoleId(id);
        role.setDeleted(StateEnums.DELETED.getCode());
        this.sysRoleMapper.updateById(role);
    }

    @Override
    public SysRoleVo get(Long id) {
        SysRole sysRole = sysRoleMapper.selectById(id);
        // 拷贝属性
        SysRoleVo vo = new SysRoleVo();
        BeanUtils.copyProperties(sysRole, vo);
        // 查询这个角色存在的所有权限
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysRoleMenu.COL_ROLE_ID,id);
        List<SysRoleMenu> roleMenuList = sysRoleMenuMapper.selectList(qw);
        // 如果角色权限集合不为空，取出菜单ID集合
        if(!CollectionUtils.isEmpty(roleMenuList)) {
            // 取出权限ID集合
            List<Long> authIds = roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            vo.setAuthIds(authIds);
        }
        return vo;
    }

    @Override
    public Page<SysRole> getByPage(Page<SysRole> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysRole> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<SysRole> qw=new QueryWrapper<>();
        SysRole sysRole=new SysRole();
        BeanUtil.copyProperties(page.getParams(),sysRole);
        qw.eq(SysRole.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(sysRole.getRoleName()),SysRole.COL_ROLE_NAME,sysRole.getRoleName());
        qw.orderByAsc(SysRole.COL_ROLE_SORT);
        this.sysRoleMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<SysRole> getAll() {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysRole.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.orderByAsc(SysRole.COL_ROLE_SORT);
        return this.sysRoleMapper.selectList(qw);
    }

    @Override
    public SysRole getByRoleName(String roleName) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysRole.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(StringUtils.isNoneBlank(roleName),SysRole.COL_ROLE_NAME,roleName);
        return this.sysRoleMapper.selectOne(qw);
    }
}
