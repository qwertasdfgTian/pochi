package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.enums.StateEnums;
import com.lt.pojo.ShopPack;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.mapper.ShopOrderPayMapper;
import com.lt.pojo.ShopOrderPay;
import com.lt.service.ShopOrderPayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/19 23:51
 * @Version 1.0
 */
@Service
public class ShopOrderPayServiceImpl implements ShopOrderPayService {

    @Autowired
    private ShopOrderPayMapper shopOrderPayMapper;

    @Override
    public Page<ShopOrderPay> getByPage(Page<ShopOrderPay> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopOrderPay> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopOrderPay> qw=new QueryWrapper<>();
        ShopOrderPay shopOrderPay=new ShopOrderPay();
        BeanUtil.copyProperties(page.getParams(),shopOrderPay);
        qw.eq(ShopOrderPay.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(shopOrderPay.getCreateBy()),ShopOrderPay.COL_CREATE_BY,shopOrderPay.getCreateBy());
        qw.eq(StringUtils.isNotBlank(shopOrderPay.getOrderNo()),ShopOrderPay.COL_ORDER_NO,shopOrderPay.getOrderNo());
        qw.eq(null!=shopOrderPay.getOrderId(),ShopOrderPay.COL_ORDER_ID,shopOrderPay.getOrderId());
        qw.eq(StringUtils.isNotBlank(shopOrderPay.getOutTradeNo()),ShopOrderPay.COL_OUT_TRADE_NO,shopOrderPay.getOutTradeNo());
        qw.eq(null!=shopOrderPay.getStatus(),ShopOrderPay.COL_STATUS,shopOrderPay.getStatus());
        qw.orderByDesc(ShopOrderPay.COL_CREATE_TIME);
        this.shopOrderPayMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public ShopOrderPay get(Long id) {
        return this.shopOrderPayMapper.selectById(id);
    }

    @Override
    public ShopOrderPay getByOrderId(Long id) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,id);
        qw.orderByDesc(ShopOrderPay.COL_CREATE_TIME);
        List<ShopOrderPay> list = this.shopOrderPayMapper.selectList(qw);
        if(list.size()!=0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        ShopOrderPay shopOrderPay=new ShopOrderPay();
        shopOrderPay.setId(id);
        shopOrderPay.setDeleted(StateEnums.DELETED.getCode());
        this.shopOrderPayMapper.updateById(shopOrderPay);
    }

    @Override
    public BigDecimal getConsumption(LoginUser loginUser) {
        return this.shopOrderPayMapper.getConsumption(loginUser.getUsername());
    }
}
