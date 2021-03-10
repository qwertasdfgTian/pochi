package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopCartItem;

public interface ShopCartItemMapper extends BaseMapper<ShopCartItem> {

    /**
     * 求购物车商品数量和
     * @Param: username
    */
    Integer getProductCountByUser(String username);
}