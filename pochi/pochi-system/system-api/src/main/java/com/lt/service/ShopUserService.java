package com.lt.service;

import com.lt.dto.ShopUserBindDto;
import com.lt.dto.UserDto;
import com.lt.pojo.ShopUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.vo.*;

import java.util.List;

public interface ShopUserService {

    /**
     *  根据openid查询数据库
    */
    ShopUser getByOpenId(String openId);

    /**
     * 用户注册
     * @Param: toShopUser
    */
    void register(ShopUser toShopUser);

    /**
     * 用户绑定
     * @Param: shopUserBindDto
    */
    ShopUser bindUser(ShopUserBindDto shopUserBindDto);

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<ShopUser> getByPage(Page<ShopUser> page);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ShopUserVo get(Long id);

    /**
     * 根据id删除
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 解封
     *
     * @param id
     */
    void enableById(Long id);

    /**
     * 封禁
     *
     * @param id
     */
    void disableById(Long id);

    /**
     * 查询累计消费最多的前十个用户
     * @return
     */
    List<ShopUserStatisticVo> getTopStatistic();

    /**
     * 根据手机号查询openid
     * @Param: phone
    */
    ShopUser getByPhone(String phone);

    /**
     * 查询我的钱包
     * @Param: loginUser
    */
    MyWalletVo getMyWallet(LoginUser loginUser);

    /**
     * 修改用户信息
     * @Param: loginUser
    */
    LoginUser update(LoginUser loginUser, UserDto userDto);

}
