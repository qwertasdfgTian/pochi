package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.ShopUserBindDto;
import com.lt.dto.UserDto;
import com.lt.enums.ResultEnums;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.mapper.ShopUserStatisticMapper;
import com.lt.pojo.ShopUserStatistic;
import com.lt.utils.IdWorker;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.pojo.ShopUser;
import com.lt.mapper.ShopUserMapper;
import com.lt.service.ShopUserService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopUserServiceImpl implements ShopUserService{

    @Autowired
    private ShopUserMapper shopUserMapper;
    @Autowired
    private ShopUserStatisticMapper shopUserStatisticMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    public ShopUser getByOpenId(String openId) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUser.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.eq(ShopUser.COL_STATUS,1);
        qw.eq(ShopUser.COL_OPENID,openId);
        return this.shopUserMapper.selectOne(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(ShopUser shopUser) {
        // ????????????
        long userId = idWorker.nextId();
        shopUser.setId(userId);
        shopUser.setPhone((idWorker.nextId())+"");
        this.shopUserMapper.insert(shopUser);
        // ??????????????????????????????
        ShopUserStatistic statistic = new ShopUserStatistic();
        long statisticId = idWorker.nextId();
        statistic.setId(statisticId);
        statistic.setUserId(userId);
        this.shopUserStatisticMapper.insert(statistic);
    }

    @Override
    public ShopUser bindUser(ShopUserBindDto shopUserBindDto) {
        Integer bindType = shopUserBindDto.getBindType();
        if (StateEnums.NEW_USER.getCode().equals(bindType)) {
            // ???????????????????????????
            QueryWrapper qw1=new QueryWrapper();
            qw1.eq(ShopUser.COL_DELETED, StateEnums.NOT_DELETED.getCode());
            qw1.eq(ShopUser.COL_STATUS,1);
            qw1.eq(ShopUser.COL_PHONE,shopUserBindDto.getPhone());
            ShopUser shopUser = this.shopUserMapper.selectOne(qw1);
            if (shopUser != null) {
                throw new PochiException(ResultEnums.USER_REAL_EXISTS);
            }
            // ????????????????????????????????????openid?????????openid????????????????????? ??????????????????
            LoginUser loginUser = ShiroUtils.getLoginUser();
            String openId = loginUser.getOpenId();
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopUser.COL_DELETED, StateEnums.NOT_DELETED.getCode());
            qw.eq(ShopUser.COL_STATUS,1);
            qw.eq(ShopUser.COL_OPENID,openId);
            shopUser = this.shopUserMapper.selectOne(qw);

            shopUser.setPhone(shopUserBindDto.getPhone());
            shopUser.setPassword(shopUserBindDto.getPassword());
            //???????????????????????????
            if(StringUtils.isNotBlank(shopUser.getPhone())&&StringUtils.isNotBlank(shopUser.getPassword())){
                this.shopUserMapper.updateById(shopUser);
            }
            return shopUser;
        } else {
            LoginUser loginUser = ShiroUtils.getLoginUser();
            // ??????????????????
            // ???????????????????????????openid
            QueryWrapper qw1=new QueryWrapper();
            qw1.eq(ShopUser.COL_DELETED, StateEnums.NOT_DELETED.getCode());
            qw1.eq(ShopUser.COL_STATUS,1);
            qw1.eq(ShopUser.COL_PHONE,shopUserBindDto.getPhone());
            ShopUser shopUser = this.shopUserMapper.selectOne(qw1);
            if (shopUser == null) {
                throw new PochiException(ResultEnums.USER_NOT_FOUND);
            }
            if (!shopUser.getPassword().equals(shopUserBindDto.getPassword())) {
                throw new PochiException("???????????????");
            }
            shopUser.setOpenId(loginUser.getOpenId());
            this.shopUserMapper.updateById(shopUser);
            // ????????????????????????ID???????????????????????????????????????????????????
            this.shopUserMapper.deleteById(loginUser.getId());
            return shopUser;
        }
    }

    @Override
    public Page<ShopUser> getByPage(Page<ShopUser> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopUser> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopUser> qw=new QueryWrapper<>();
        ShopUser shopUser=new ShopUser();
        BeanUtil.copyProperties(page.getParams(),shopUser);
        qw.eq(ShopUser.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(null!=shopUser.getStatus(),ShopUser.COL_STATUS,shopUser.getStatus());
        qw.eq(StringUtils.isNotBlank(shopUser.getPhone()),ShopUser.COL_PHONE,shopUser.getPhone());
        qw.eq(StringUtils.isNotBlank(shopUser.getNickname()),ShopUser.COL_NICKNAME,shopUser.getNickname());
        this.shopUserMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public ShopUserVo get(Long id) {
        ShopUser shopUser = this.shopUserMapper.selectById(id);
        ShopUserVo vo = new ShopUserVo();
        BeanUtils.copyProperties(shopUser, vo);
        // ??????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUserStatistic.COL_USER_ID,id);
        ShopUserStatistic statistic = this.shopUserStatisticMapper.selectOne(qw);
        vo.setShopUserStatistic(statistic);
        return vo;
    }

    @Override
    public void delete(Long id) {
        ShopUser shopUser=new ShopUser();
        shopUser.setId(id);
        shopUser.setDeleted(StateEnums.DELETED.getCode());
        this.shopUserMapper.updateById(shopUser);
    }

    @Override
    public void enableById(Long id) {
        ShopUser shopUser=new ShopUser();
        shopUser.setId(id);
        shopUser.setStatus(1);
        this.shopUserMapper.updateById(shopUser);
    }

    @Override
    public void disableById(Long id) {
        ShopUser shopUser=new ShopUser();
        shopUser.setId(id);
        shopUser.setStatus(0);
        this.shopUserMapper.updateById(shopUser);
    }

    @Override
    public List<ShopUserStatisticVo> getTopStatistic() {
        List<ShopUserStatistic> list = this.shopUserStatisticMapper.getTopStatistic();
        List<Long> userIds = list.stream().map(ShopUserStatistic::getUserId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.in(ShopUser.COL_ID,userIds);
        List<ShopUser> userList = this.shopUserMapper.selectList(qw);
        List<ShopUserStatisticVo> voList = new ArrayList<>();
        for (ShopUserStatistic statistic : list) {
            ShopUser shopUser = userList.stream().filter(u -> u.getId().equals(statistic.getUserId())).findFirst().orElse(null);
            if (shopUser == null) {
                continue;
            }
            ShopUserStatisticVo consume = new ShopUserStatisticVo();
            consume.setUserName(shopUser.getNickname() + shopUser.getPhone());
            consume.setName("????????????");
            consume.setCount(statistic.getConsumeAmount());
            voList.add(consume);

            ShopUserStatisticVo order = new ShopUserStatisticVo();
            order.setUserName(shopUser.getNickname() + shopUser.getPhone());
            order.setName("????????????");
            order.setCount(new BigDecimal(statistic.getOrderCount()));
            voList.add(order);

            ShopUserStatisticVo coupon = new ShopUserStatisticVo();
            coupon.setUserName(shopUser.getNickname() + shopUser.getPhone());
            coupon.setName("????????????");
            coupon.setCount(new BigDecimal(statistic.getCouponCount()));
            voList.add(coupon);

            ShopUserStatisticVo comment = new ShopUserStatisticVo();
            comment.setUserName(shopUser.getNickname() + shopUser.getPhone());
            comment.setName("?????????");
            comment.setCount(new BigDecimal(statistic.getCommentCount()));
            voList.add(comment);

            ShopUserStatisticVo retOrder = new ShopUserStatisticVo();
            retOrder.setUserName(shopUser.getNickname() + shopUser.getPhone());
            retOrder.setName("?????????");
            retOrder.setCount(new BigDecimal(statistic.getReturnOrderCount()));
            voList.add(retOrder);

            ShopUserStatisticVo login = new ShopUserStatisticVo();
            login.setUserName(shopUser.getNickname() + shopUser.getPhone());
            login.setName("????????????");
            login.setCount(new BigDecimal(statistic.getLoginCount()));
            voList.add(login);
        }
        return voList;
    }

    @Override
    public ShopUser getByPhone(String phone) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUser.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.eq(ShopUser.COL_STATUS, StateEnums.ENABLED.getCode());
        qw.eq(ShopUser.COL_PHONE,phone);
        return this.shopUserMapper.selectOne(qw);
    }

    @Override
    public MyWalletVo getMyWallet(LoginUser loginUser) {
        MyWalletVo myWalletVo=new MyWalletVo();
        // ??????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUser.COL_PHONE,loginUser.getUsername());
        ShopUser shopUser = this.shopUserMapper.selectOne(qw);
        myWalletVo.setMyPoint(shopUser.getPoint());
        // ?????????????????????
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopUserStatistic.COL_USER_ID,shopUser.getId());
        ShopUserStatistic shopUserStatistic = this.shopUserStatisticMapper.selectOne(qw1);
        myWalletVo.setCouponCount(shopUserStatistic.getCouponCount());
        return myWalletVo;
    }

    @Override
    public LoginUser update(LoginUser loginUser, UserDto userDto) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUser.COL_PHONE,loginUser.getUsername());
        ShopUser shopUser=new ShopUser();
        shopUser.setId(loginUser.getId());
        if(StringUtils.isNotBlank(userDto.getHeader()))
            shopUser.setHeader(userDto.getHeader());
        if(StringUtils.isNotBlank(userDto.getNickName()))
            shopUser.setNickname(userDto.getNickName());
        if (null!=userDto.getSex())
            shopUser.setGender(userDto.getSex());
        if (StringUtils.isNotBlank(userDto.getBirthday()))
            shopUser.setBirthday(userDto.getBirthday());
        this.shopUserMapper.update(shopUser,qw);
        // ??????????????????????????????
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopUser.COL_PHONE,loginUser.getUsername());
        ShopUser shopUser1 = this.shopUserMapper.selectOne(qw1);
        return shopUser1.toLoginUser();
    }

}
