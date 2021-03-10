package com.lt.service;

import com.lt.pojo.ShopCartItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;

import java.util.List;

public interface ShopCartItemService {

    /**
     * 加入购物车
     * @param shopCartItem
     */
    void save(ShopCartItem shopCartItem, LoginUser loginUser);

    /**
     * 查询当前登录用户购物车有多少商品
     * @return
     */
    Integer getProductCount(LoginUser loginUser);

    /**
     * 分页查询
     * @param page
     * @return
     */
    List<ShopCartItem> getByPage(Page<ShopCartItem> page,LoginUser loginUser);

    /**
     * 根据id集合删除
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 移入收藏
     * @param ids
     */
    void moveToCollection(List<Long> ids,LoginUser loginUser);
}
