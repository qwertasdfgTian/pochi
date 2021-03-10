package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.ShopCouponDto;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.mapper.*;
import com.lt.pojo.*;
import com.lt.service.ShopCouponService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.LoginUser;
import com.lt.vo.MyBothCouponVo;
import com.lt.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopCouponServiceImpl implements ShopCouponService{

    @Autowired
    private ShopCouponMapper shopCouponMapper;
    @Autowired
    private ShopCouponCategoryMapper shopCouponCategoryMapper;
    @Autowired
    private ShopCouponProductMapper shopCouponProductMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ShopUserStatisticMapper shopUserStatisticMapper;
    @Autowired
    private ShopCouponHistoryMapper shopCouponHistoryMapper;

    @Override
    public void save(ShopCouponDto shopCouponDto, LoginUser loginUser) {
        long couponId = idWorker.nextId();
        // 复制属性
        ShopCoupon shopCoupon = new ShopCoupon();
        BeanUtils.copyProperties(shopCouponDto, shopCoupon);
        // 设置默认值
        shopCoupon.setId(couponId);
        shopCoupon.setCreateBy(loginUser.getUsername());
        shopCoupon.setUpdateBy(loginUser.getUsername());
        shopCoupon.setRestCount(shopCoupon.getPublishCount());
        this.shopCouponMapper.insert(shopCoupon);
        // 如果有分类，添加优惠券-分类关联
        if (StateEnums.CATEGORY.getCode().equals(shopCoupon.getCouponType())) {
            if(shopCouponDto.getCategoryList().size()!=0){
                List<ShopCouponCategory> couponCategoryList = shopCouponDto.getCategoryList().stream().map(e -> new ShopCouponCategory(couponId, e.getId())).collect(Collectors.toList());
                for (ShopCouponCategory shopCouponCategory : couponCategoryList) {
                    this.shopCouponCategoryMapper.insert(shopCouponCategory);
                }
            }
        }
        // 如果有商品，添加优惠券-商品关联
        if (StateEnums.PRODUCT.getCode().equals(shopCoupon.getCouponType())) {
            if(shopCouponDto.getProductList().size()!=0){
                List<ShopCouponProduct> couponProductList = shopCouponDto.getProductList().stream().map(e -> new ShopCouponProduct(couponId, e.getId())).collect(Collectors.toList());
                for (ShopCouponProduct shopCouponProduct : couponProductList) {
                    this.shopCouponProductMapper.insert(shopCouponProduct);
                }
            }
        }
    }

    @Override
    public void delete(Long id) {
        ShopCoupon shopCoupon=new ShopCoupon();
        shopCoupon.setId(id);
        shopCoupon.setDeleted(StateEnums.DELETED.getCode());
        this.shopCouponMapper.updateById(shopCoupon);
    }

    @Override
    public void down(Long id) {
        ShopCoupon shopCoupon=new ShopCoupon();
        shopCoupon.setId(id);
        shopCoupon.setStatus(StateEnums.COUPON_TIMEOUT.getCode());
        this.shopCouponMapper.updateById(shopCoupon);
        // 更新优惠券领取记录状态
        ShopCouponHistory shopCouponHistory=new ShopCouponHistory();
        shopCouponHistory.setUseStatus(StateEnums.COUPON_EXPIRED.getCode());
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq(ShopCouponHistory.COL_COUPON_ID,shopCoupon.getId());
        this.shopCouponHistoryMapper.update(shopCouponHistory,queryWrapper);
    }

    @Override
    public Page<ShopCoupon> getByPage(Page<ShopCoupon> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopCoupon> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopCoupon> qw=new QueryWrapper<>();
        ShopCoupon shopCoupon=new ShopCoupon();
        BeanUtil.copyProperties(page.getParams(),shopCoupon);
        qw.eq(ShopCoupon.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(shopCoupon.getName()),ShopCoupon.COL_NAME,shopCoupon.getName());
        qw.orderByDesc(ShopCoupon.COL_CREATE_TIME);
        this.shopCouponMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<ShopCoupon> getProductCoupon(Long productId,LoginUser loginUser) {
        // 查询商品信息
        ShopProduct product = this.shopCouponMapper.getInfoById(productId);
        // 查询全场通用优惠券
        List<ShopCoupon> bothList = this.shopCouponMapper.getBothCoupon();
        // 查询该商品所在分类的优惠券
        List<ShopCoupon> categoryCouponList = this.shopCouponMapper.getByCategoryId(product.getCategoryId());
        // 查询该商品的优惠券
        List<ShopCoupon> productCouponList = this.shopCouponMapper.getByProductId(productId);
        bothList.addAll(categoryCouponList);
        bothList.addAll(productCouponList);
        if (!CollectionUtils.isEmpty(bothList)) {
            // 根据优惠券id集合和用户账号查询优惠券领取历史，判断优惠券是否已经被领取
            List<Long> couponIds = bothList.stream().map(ShopCoupon::getId).collect(Collectors.toList());
            String username = loginUser.getUsername();
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopCouponHistory.COL_CREATE_BY,username);
            qw.in(ShopCouponHistory.COL_COUPON_ID,couponIds);
            List<ShopCouponHistory> historyList = this.shopCouponHistoryMapper.selectList(qw);
            if (!CollectionUtils.isEmpty(historyList)) {
                bothList.forEach(c -> {
                    ShopCouponHistory history = historyList.stream().filter(h -> h.getCouponId().equals(c.getId())).findFirst().orElse(null);
                    if (history != null) {
                        c.setStatus(StateEnums.COUPON_RECEIVE.getCode());
                    }
                });
            }
        }
        return bothList;
    }

    @Override
    public void updateTimeoutCoupon() {
        ShopCoupon shopCoupon =new ShopCoupon();
        shopCoupon.setStatus(StateEnums.COUPON_TIMEOUT.getCode());
        QueryWrapper qw=new QueryWrapper();
        qw.lt(ShopCoupon.COL_END_TIME,DateUtils.newDateTime());
        qw.eq(ShopCoupon.COL_STATUS,StateEnums.COUPON_NORMAL.getCode());
        List<ShopCoupon> list = this.shopCouponMapper.selectList(qw);
        this.shopCouponMapper.update(shopCoupon,qw);
        // 更新优惠券领取记录状态
        ShopCouponHistory shopCouponHistory=new ShopCouponHistory();
        shopCouponHistory.setUseStatus(StateEnums.COUPON_EXPIRED.getCode());
        for (ShopCoupon coupon : list) {
            QueryWrapper queryWrapper=new QueryWrapper();
            queryWrapper.eq(ShopCouponHistory.COL_COUPON_ID,coupon.getId());
            this.shopCouponHistoryMapper.update(shopCouponHistory,queryWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void catchCoupon(ShopCoupon shopCoupon,LoginUser loginUser) {
        // 1. 查询优惠券数据，校验剩余数量
        ShopCoupon coupon = this.shopCouponMapper.selectById(shopCoupon.getId());
        if (coupon.getRestCount() <= 0) {
            throw new PochiException("优惠券已经领取完毕");
        }
        // 2. 更新领取数量和剩余数量
        coupon.setRestCount(coupon.getRestCount() - 1);
        coupon.setReceiveCount(coupon.getReceiveCount() + 1);
        this.shopCouponMapper.updateById(coupon);
        // 3. 在优惠券历史表中插入一条领取记录，状态为未使用
        ShopCouponHistory shopCouponHistory = new ShopCouponHistory();
        shopCouponHistory.setCouponId(coupon.getId());
        shopCouponHistory.setCreateBy(loginUser.getUsername());
        shopCouponHistory.setAmount(coupon.getAmount());
        shopCouponHistory.setMinPoint(coupon.getMinPoint());
        this.shopCouponHistoryMapper.insert(shopCouponHistory);
        // 4. 在用户统计表中将优惠券数量+1
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopUserStatistic.COL_USER_ID,loginUser.getId());
        ShopUserStatistic statistic = this.shopUserStatisticMapper.selectOne(qw1);
        statistic.setCouponCount(statistic.getCouponCount() + 1);
        this.shopUserStatisticMapper.updateById(statistic);
    }

    @Override
    public ShopCoupon get(Long id) {
        return this.shopCouponMapper.selectById(id);
    }

    @Override
    public List<ShopCouponHistory> getHistoryList(Long id) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopCouponHistory.COL_COUPON_ID,id);
        return shopCouponHistoryMapper.selectList(qw);
    }

    @Override
    public MyBothCouponVo getAllProductCoupon(LoginUser loginUser) {
        // 查询所有优惠券
        List<ShopCoupon> bothList = this.shopCouponMapper.selectList(new QueryWrapper<>());
        // 创建一个空的我的优惠券的集合
        MyBothCouponVo myBothCouponVo=new MyBothCouponVo();
        if (!CollectionUtils.isEmpty(bothList)) {
            // 根据优惠券id集合和用户账号查询优惠券领取历史，判断优惠券是否已经被领取
            List<Long> couponIds = bothList.stream().map(ShopCoupon::getId).collect(Collectors.toList());
            String username = loginUser.getUsername();
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopCouponHistory.COL_CREATE_BY,username);
            qw.in(ShopCouponHistory.COL_COUPON_ID,couponIds);
            List<ShopCouponHistory> historyList = this.shopCouponHistoryMapper.selectList(qw);
            List<ShopCoupon> list1=new ArrayList<>();
            List<ShopCoupon> list2=new ArrayList<>();
            List<ShopCoupon> list3=new ArrayList<>();
            if (!CollectionUtils.isEmpty(historyList)) {
                bothList.forEach(c -> {
                    ShopCouponHistory history = historyList.stream().filter(h -> h.getCouponId().equals(c.getId())).findFirst().orElse(null);
                    if (history != null && history.getUseStatus()==StateEnums.COUPON_EXPIRED.getCode()) {
                        list1.add(c);
                    }
                    if (history != null && history.getUseStatus()==StateEnums.COUPON_USED.getCode()) {
                        list2.add(c);
                    }
                    if (history != null && history.getUseStatus()==StateEnums.COUPON_NOTUSED.getCode()) {
                        list3.add(c);
                    }
                });
                myBothCouponVo.setExpiredCoupon(list1);
                myBothCouponVo.setUsedCoupon(list2);
                myBothCouponVo.setNotUsedCoupon(list3);
            }
        }
        return myBothCouponVo;
    }
}
