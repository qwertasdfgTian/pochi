package com.lt.service;

import com.lt.pojo.ShopUserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ShopUserAddressService {

    /**
     * 添加地址
     * @param shopUserAddress
     */
    void save(ShopUserAddress shopUserAddress);

    /**
     * 修改地址
     * @param shopUserAddress
     */
    void update(ShopUserAddress shopUserAddress);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    ShopUserAddress get(Long id);

    /**
     * 查询用户收货地址列表
     * @return
     */
    List<ShopUserAddress> getUserAddress();

}
