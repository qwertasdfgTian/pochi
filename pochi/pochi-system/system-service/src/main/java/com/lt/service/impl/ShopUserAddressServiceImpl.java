package com.lt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.enums.StateEnums;
import com.lt.utils.IdWorker;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.mapper.ShopUserAddressMapper;
import com.lt.pojo.ShopUserAddress;
import com.lt.service.ShopUserAddressService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopUserAddressServiceImpl implements ShopUserAddressService{

    @Autowired
    private ShopUserAddressMapper shopUserAddressMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ShopUserAddress shopUserAddress) {
        // 处理默认收货地址
        Integer defaultStatus = shopUserAddress.getDefaultStatus();
        LoginUser loginUser = ShiroUtils.getLoginUser();
        if(StateEnums.ADDRESS_DEFAULT.getCode().equals(defaultStatus)) {
            // 当前地址是默认的收货地址，将该用户其他的地址全部修改为非默认
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopUserAddress.COL_USER_ID,loginUser.getId());
            qw.eq(ShopUserAddress.COL_DEFAULT_STATUS,StateEnums.ADDRESS_DEFAULT.getCode());
            ShopUserAddress shopUserAddress1 =new ShopUserAddress();
            shopUserAddress1.setDefaultStatus(StateEnums.ADDRESS_NOT_DEFAULT.getCode());
            this.shopUserAddressMapper.update(shopUserAddress1,qw);
        }
        shopUserAddress.setUserId(loginUser.getId());
        shopUserAddress.setId(idWorker.nextId());
        this.shopUserAddressMapper.insert(shopUserAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShopUserAddress shopUserAddress) {
        Integer defaultStatus = shopUserAddress.getDefaultStatus();
        LoginUser loginUser = ShiroUtils.getLoginUser();
        if(StateEnums.ADDRESS_DEFAULT.getCode().equals(defaultStatus)) {
            // 当前地址是默认的收货地址，将该用户其他的地址全部修改为非默认
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopUserAddress.COL_USER_ID,loginUser.getId());
            qw.eq(ShopUserAddress.COL_DEFAULT_STATUS,StateEnums.ADDRESS_DEFAULT.getCode());
            ShopUserAddress shopUserAddress1 =new ShopUserAddress();
            shopUserAddress1.setDefaultStatus(StateEnums.ADDRESS_NOT_DEFAULT.getCode());
            this.shopUserAddressMapper.update(shopUserAddress1,qw);
        }
        this.shopUserAddressMapper.updateById(shopUserAddress);
    }

    @Override
    public void delete(Long id) {
        this.shopUserAddressMapper.deleteById(id);
    }

    @Override
    public ShopUserAddress get(Long id) {
        QueryWrapper qw=new QueryWrapper();
        qw.orderByDesc(ShopUserAddress.COL_DEFAULT_STATUS);
        qw.orderByDesc(ShopUserAddress.COL_CREATE_TIME);
        qw.eq(ShopUserAddress.COL_ID,id);
        return this.shopUserAddressMapper.selectOne(qw);
    }

    @Override
    public List<ShopUserAddress> getUserAddress() {
        Long userId = ShiroUtils.getLoginUser().getId();
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUserAddress.COL_USER_ID,userId);
        qw.orderByDesc(ShopUserAddress.COL_DEFAULT_STATUS);
        qw.orderByDesc(ShopUserAddress.COL_CREATE_TIME);
        return this.shopUserAddressMapper.selectList(qw);
    }
}
