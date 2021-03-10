package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.mapper.SysHelpMapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysHelp;
import com.lt.service.SysHelpService;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysHelpServiceImpl implements SysHelpService {

    @Autowired
    private SysHelpMapper sysHelpMapper;

    @Override
    public void save(SysHelp sysHelp) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        sysHelp.setCreatedBy(loginUser.getUsername());
        sysHelp.setUpdateBy(loginUser.getUsername());
        this.sysHelpMapper.insert(sysHelp);
    }

    @Override
    public void update(SysHelp sysHelp) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        sysHelp.setUpdateBy(loginUser.getUsername());
        this.sysHelpMapper.updateById(sysHelp);
    }

    @Override
    public void delete(Long id) {
        SysHelp sysHelp=new SysHelp();
        sysHelp.setId(id);
        sysHelp.setDeleted(StateEnums.DELETED.getCode());
        this.sysHelpMapper.updateById(sysHelp);
    }

    @Override
    public SysHelp get(Long id) {
        return this.sysHelpMapper.selectById(id);
    }

    @Override
    public Page<SysHelp> getByPage(Page<SysHelp> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysHelp> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<SysHelp> qw=new QueryWrapper<>();
        SysHelp sysHelp=new SysHelp();
        BeanUtil.copyProperties(page.getParams(),sysHelp);
        qw.eq(SysHelp.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(sysHelp.getTitle()),SysHelp.COL_TITLE,sysHelp.getTitle());
        this.sysHelpMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }
}
