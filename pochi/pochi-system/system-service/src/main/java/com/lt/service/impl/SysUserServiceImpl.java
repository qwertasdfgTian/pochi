package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.SysUserDto;
import com.lt.mapper.SysRoleMapper;
import com.lt.mapper.SysUserMapper;
import com.lt.mapper.SysUserRoleMapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysRole;
import com.lt.pojo.SysUser;
import com.lt.pojo.SysUserRole;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.utils.StringUtils;
import com.lt.vo.Page;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lt.service.SysUserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void updateLoginTime(String username) {
        SysUser sysUser=new SysUser();
        sysUser.setLoginTime(DateUtils.newDateTime());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(StringUtils.isNotBlank(username),SysUser.COL_USERNAME,username);
        this.sysUserMapper.update(sysUser,qw);
    }

    @Override
    public SysUser getByUsername(String username) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(StringUtils.isNotBlank(username),SysUser.COL_USERNAME,username);
        return this.sysUserMapper.selectOne(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUserDto sysUserDto) {
        SysUser sysUser=new SysUser();
        BeanUtil.copyProperties(sysUserDto,sysUser);
        long userId=idWorker.nextId();
        sysUser.setId(userId);
        this.sysUserMapper.insert(sysUser);
        // 如果角色id存在，存入用户角色表
        if (sysUserDto.getSysRole() != null && sysUserDto.getSysRole().getRoleId() != null) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(sysUserDto.getSysRole().getRoleId());
            this.sysUserRoleMapper.insert(sysUserRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserDto sysUserDto) {
        // 拷贝属性
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserDto, sysUser);
        this.sysUserMapper.updateById(sysUser);
        // 不管前端有没有选择角色，我们都把旧的角色信息删掉，如果选择了，就再添加新的角色信息
        QueryWrapper qw =new QueryWrapper();
        qw.eq(null!=sysUser.getId(),SysUserRole.COL_USER_ID,sysUser.getId());
        this.sysUserRoleMapper.delete(qw);
        // 如果角色id存在，存入用户角色表
        if (sysUserDto.getSysRole() != null && sysUserDto.getSysRole().getRoleId() != null) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setRoleId(sysUserDto.getSysRole().getRoleId());
            this.sysUserRoleMapper.deleteById(sysUser.getId());
            this.sysUserRoleMapper.insert(sysUserRole);
        }
    }

    @Override
    public void delete(Long id) {
        SysUser sysUser=new SysUser();
        sysUser.setId(id);
        sysUser.setDeleted(StateEnums.DELETED.getCode());
        this.sysUserMapper.updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        SysUser sysUser=this.sysUserMapper.selectById(id);
        sysUser.setStatus(StateEnums.ENABLED.getCode());
        this.sysUserMapper.updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        SysUser sysUser=this.sysUserMapper.selectById(id);
        sysUser.setStatus(StateEnums.NOT_ENABLE.getCode());
        this.sysUserMapper.updateById(sysUser);
    }

    @Override
    public Page<SysUser> getByPage(Page<SysUser> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<SysUser> qw=new QueryWrapper<>();
        SysUser sysUser=new SysUser();
        BeanUtil.copyProperties(page.getParams(),sysUser);
        qw.eq(SysUser.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(StringUtils.isNotBlank(sysUser.getUsername()),SysUser.COL_USERNAME,sysUser.getUsername());
        qw.like(StringUtils.isNotBlank(sysUser.getNickName()),SysUser.COL_NICK_NAME,sysUser.getNickName());
        qw.eq(null!=sysUser.getStatus(),SysUser.COL_STATUS,sysUser.getStatus());
        this.sysUserMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserDto get(Long id) {
        SysUser user = this.sysUserMapper.selectById(id);
        // 拷贝信息
        SysUserDto sysUserDto = new SysUserDto();
        BeanUtils.copyProperties(user, sysUserDto);
        // 查询角色信息
        QueryWrapper qw =new QueryWrapper();
        qw.eq(null!=user.getId(),SysUserRole.COL_USER_ID,user.getId());
        List<SysUserRole> sysUserRoleList = this.sysUserRoleMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(sysUserRoleList)) {
            // 说明有角色信息，取出角色ID
            List<Long> roleIds = sysUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            // 根据角色ID去查询所有的角色信息
            List<SysRole> roleList = this.sysRoleMapper.selectBatchIds(roleIds);
            if (!CollectionUtils.isEmpty(roleList)&&(roleList.get(0).getDeleted()!=StateEnums.DELETED.getCode())) {
                sysUserDto.setSysRole(roleList.get(0));
            }
        }else {
            sysUserDto.setSysRole(new SysRole());
        }
        return sysUserDto;
    }
}
