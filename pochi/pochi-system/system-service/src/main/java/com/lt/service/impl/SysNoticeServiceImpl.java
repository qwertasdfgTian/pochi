package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.mapper.SysNoticeMapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysNotice;
import com.lt.service.SysNoticeService;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;

@Service
public class SysNoticeServiceImpl implements SysNoticeService {

    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    @Override
    public void save(SysNotice sysNotice) {
        // 创建人默认值
        LoginUser loginUser = ShiroUtils.getLoginUser();
        sysNotice.setCreatedBy(loginUser.getUsername());
        this.sysNoticeMapper.insert(sysNotice);
    }

    @Override
    public void update(SysNotice sysNotice) {
        this.sysNoticeMapper.updateById(sysNotice);
    }

    @Override
    public SysNotice get(Long id) {
        return sysNoticeMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        SysNotice sysNotice=new SysNotice();
        sysNotice.setNoticeId(id);
        sysNotice.setDeleted(1);
        this.sysNoticeMapper.updateById(sysNotice);
    }

    @Override
    public void enable(Long id) {
        SysNotice sysNotice = this.sysNoticeMapper.selectById(id);
        sysNotice.setEnabled(StateEnums.ENABLED.getCode());
        this.sysNoticeMapper.updateById(sysNotice);
    }

    @Override
    public void disable(Long id) {
        SysNotice sysNotice = this.sysNoticeMapper.selectById(id);
        sysNotice.setEnabled(StateEnums.NOT_ENABLE.getCode());
        this.sysNoticeMapper.updateById(sysNotice);
    }

    @Override
    public Page<SysNotice> getByPage(Page<SysNotice> page) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysNotice> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<SysNotice> qw=new QueryWrapper<>();
        SysNotice sysNotice=new SysNotice();
        BeanUtil.copyProperties(page.getParams(),sysNotice);
        qw.eq(SysNotice.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(sysNotice.getNoticeTitle()),SysNotice.COL_NOTICE_TITLE,sysNotice.getNoticeTitle());
        qw.eq(sysNotice.getEnabled()!=null,SysNotice.COL_ENABLED,sysNotice.getEnabled());
        qw.orderByAsc(SysNotice.COL_SORTED);
        this.sysNoticeMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<SysNotice> getNoticeList() {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(SysNotice.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(SysNotice.COL_ENABLED,1);
        qw.orderByAsc(SysNotice.COL_SORTED);
        return this.sysNoticeMapper.selectList(qw);
    }
}
