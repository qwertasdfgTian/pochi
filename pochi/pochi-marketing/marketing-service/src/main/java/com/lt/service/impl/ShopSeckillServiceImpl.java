package com.lt.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.ShopSeckillDto;
import com.lt.enums.StateEnums;
import com.lt.mapper.ShopSeckillMapper;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopCouponHistory;
import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopSeckill;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Service(methods = {@Method(name = "save", retries = 0)},timeout = 50000)
public class ShopSeckillServiceImpl implements ShopSeckillService{

    @Autowired
    private ShopSeckillMapper shopseckillMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    public ShopSeckill save(ShopSeckillDto secKillDto, LoginUser loginUser) {
        // 复制属性
        ShopSeckill shopSeckill=new ShopSeckill();
        BeanUtils.copyProperties(secKillDto, shopSeckill);
        shopSeckill.setId(idWorker.nextId());
        shopSeckill.setProductId(secKillDto.getProductId());
        shopSeckill.setCreateBy(loginUser.getUsername());
        shopSeckill.setUpdateBy(loginUser.getUsername());
        if(DateUtils.newDateTime().compareTo(shopSeckill.getEndTime()) >= 0){
            // 说明时间已经过期
            shopSeckill.setStatus(StateEnums.SECKILL_OVER.getCode());
        }
        else if(DateUtils.newDateTime().compareTo(shopSeckill.getEndTime()) < 0 && DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) > 0) {
            // 说明活动已经开始
            shopSeckill.setStatus(StateEnums.SECKILL_START.getCode());
        }
        else if(DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) <= 0) {
            // 说明活动已经开始
            shopSeckill.setStatus(StateEnums.SECKILL_NOTSTART.getCode());
        }
        this.shopseckillMapper.insert(shopSeckill);
        return shopSeckill;
    }

    @Override
    public void delete(Long id) {
        ShopSeckill shopSeckill=new ShopSeckill();
        shopSeckill.setId(id);
        shopSeckill.setDeleted(StateEnums.DELETED.getCode());
        this.shopseckillMapper.updateById(shopSeckill);
    }

    @Override
    public void down(Long id) {
        ShopSeckill shopSeckill=new ShopSeckill();
        shopSeckill.setId(id);
        shopSeckill.setStatus(StateEnums.SECKILL_OVER.getCode());
        this.shopseckillMapper.updateById(shopSeckill);
    }

    @Override
    public Page<ShopSeckill> getByPage(Page<ShopSeckill> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopSeckill> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopSeckill> qw=new QueryWrapper<>();
        ShopSeckill shopSeckill=new ShopSeckill();
        BeanUtil.copyProperties(page.getParams(),shopSeckill);
        qw.eq(ShopSeckill.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.eq(null!=shopSeckill.getStatus(),ShopSeckill.COL_STATUS, shopSeckill.getStatus());
        qw.like(StringUtils.isNotBlank(shopSeckill.getName()),ShopSeckill.COL_NAME,shopSeckill.getName());
        qw.like(StringUtils.isNotBlank(shopSeckill.getProductName()),ShopSeckill.COL_PRODUCT_NAME,shopSeckill.getProductName());
        qw.orderByDesc(ShopSeckill.COL_CREATE_TIME);
        this.shopseckillMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public ShopSeckill get(Long id) {
        return this.shopseckillMapper.selectById(id);
    }

    @Override
    public void updateSecKillStatus() {
        QueryWrapper qw=new QueryWrapper();
        qw.ne(ShopSeckill.COL_STATUS,StateEnums.SECKILL_OVER.getCode());
        List<ShopSeckill> shopSeckills = this.shopseckillMapper.selectList(qw);
        for (ShopSeckill shopSeckill : shopSeckills) {
            if (DateUtils.newDateTime().compareTo(shopSeckill.getEndTime()) > 0) {
                // 说明时间已经过期
                shopSeckill.setStatus(StateEnums.SECKILL_OVER.getCode());
            } else if (DateUtils.newDateTime().compareTo(shopSeckill.getEndTime()) <= 0 && DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) >= 0) {
                // 说明活动已经开始
                shopSeckill.setStatus(StateEnums.SECKILL_START.getCode());
            } else if (DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) < 0) {
                // 说明活动已经开始
                shopSeckill.setStatus(StateEnums.SECKILL_NOTSTART.getCode());
            }
            this.shopseckillMapper.updateById(shopSeckill);
        }
    }

    @Override
    public List<ShopSeckill> getAll() {
        QueryWrapper qw=new QueryWrapper();
        qw.gt(ShopSeckill.COL_END_TIME,DateUtils.newDateTime());
        qw.eq(ShopSeckill.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.orderByAsc(ShopSeckill.COL_BEGIN_TIME);
        return this.shopseckillMapper.selectList(qw);
    }

    @Override
    public ProductSecKillVo getSecKill(Long id) throws ParseException {
        ProductSecKillVo productSecKillVo=new ProductSecKillVo();
        ShopSeckill shopSeckill = this.shopseckillMapper.selectById(id);
        if(shopSeckill!=null){
            productSecKillVo.setShopSeckill(shopSeckill);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 第一个没有开始的秒杀的时间
            Date begindate = df.parse(shopSeckill.getBeginTime());;
            Date enddate = df.parse(shopSeckill.getEndTime());
            // 获取开始时间
            Date now = df.parse(DateUtils.newDateTime());
            if(begindate.getTime()-now.getTime()<=0 && enddate.getTime()-now.getTime()>=0){
                // 已经开始记录结束时间
                productSecKillVo.setEndSecKillTime(enddate.getTime()-now.getTime());
            }else if(begindate.getTime()-now.getTime()>0){
                // 说明没有开始记录距离开始的时间
                productSecKillVo.setBeginSecKillTime(begindate.getTime()-now.getTime());
            }else {
                return null;
            }
            return productSecKillVo;
        }
        return null;
    }

    @Override
    public void updateStock(Long id) {
        ShopSeckill shopSeckill=this.shopseckillMapper.selectById(id);
        shopSeckill.setStock(shopSeckill.getStock()-1);
        shopSeckill.setUpdateTime(DateUtils.newDateTime());
        this.shopseckillMapper.updateById(shopSeckill);
        //this.shopseckillMapper.updateStock(id);
    }

    @Override
    public void updateStockAdd(Long productId) {
        ShopSeckill shopSeckill=this.shopseckillMapper.selectById(productId);
        shopSeckill.setStock(shopSeckill.getStock()+1);
        shopSeckill.setUpdateTime(DateUtils.newDateTime());
        this.shopseckillMapper.updateById(shopSeckill);
    }

    @Override
    public String selectCancelTime(Long productId) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopSeckill.COL_PRODUCT_ID,productId);
        qw.eq(ShopSeckill.COL_STATUS,StateEnums.SECKILL_START);
        return this.shopseckillMapper.selectOne(qw).getCancelTime();
    }

    @Override
    public List<ShopSeckill> getByProductId(Long productId) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopSeckill.COL_PRODUCT_ID,productId);
        qw.ne(ShopSeckill.COL_STATUS,StateEnums.SECKILL_OVER);
        return this.shopseckillMapper.selectList(qw);
    }

}
