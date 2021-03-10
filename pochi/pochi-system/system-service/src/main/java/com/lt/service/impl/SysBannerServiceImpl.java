package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.mapper.SysBannerMapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysBanner;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.lt.service.SysBannerService;
@Service
public class SysBannerServiceImpl implements SysBannerService{

    @Autowired
    private SysBannerMapper sysBannerMapper;

    @Override
    public void save(SysBanner sysBanner) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        sysBanner.setCreateBy(loginUser.getUsername());
        sysBanner.setUpdateBy(loginUser.getUsername());
        this.sysBannerMapper.insert(sysBanner);
    }

    @Override
    public void update(SysBanner sysBanner) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        sysBanner.setUpdateBy(loginUser.getUsername());
        this.sysBannerMapper.updateById(sysBanner);
    }

    @Override
    public SysBanner get(Long id) {
        return this.sysBannerMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        SysBanner sysBanner=new SysBanner();
        sysBanner.setId(id);
        sysBanner.setDeleted(1);
        this.sysBannerMapper.updateById(sysBanner);
    }

    @Override
    public void enable(Long id) {
        SysBanner banner = this.sysBannerMapper.selectById(id);
        banner.setStatus(StateEnums.ENABLED.getCode());
        this.sysBannerMapper.updateById(banner);
    }

    @Override
    public void disable(Long id) {
        SysBanner banner = this.sysBannerMapper.selectById(id);
        banner.setStatus(StateEnums.NOT_ENABLE.getCode());
        this.sysBannerMapper.updateById(banner);
    }

    @Override
    public Page<SysBanner> getByPage(Page<SysBanner> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysBanner> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<SysBanner> qw=new QueryWrapper<>();
        SysBanner sysBanner=new SysBanner();
        BeanUtil.copyProperties(page.getParams(),sysBanner);
        qw.eq(SysBanner.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(sysBanner.getStatus()!=null,SysBanner.COL_STATUS,sysBanner.getStatus());
        qw.orderByAsc(SysBanner.COL_SORT);
        qw.orderByAsc(SysBanner.COL_CREATE_TIME);
        this.sysBannerMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<SysBanner> getBannerList() {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysBanner.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(SysBanner.COL_STATUS,1);
        qw.orderByAsc(SysBanner.COL_SORT);
        return this.sysBannerMapper.selectList(qw);
    }

    @Override
    public void addClickCount(Long id) {
        SysBanner sysBanner1 = this.sysBannerMapper.selectById(id);
        SysBanner sysBanner=new SysBanner();
        sysBanner.setId(id);
        sysBanner.setClickCount(sysBanner1.getClickCount()+1);
        this.sysBannerMapper.updateById(sysBanner);
    }
}
