package com.lt.dto;

import com.lt.pojo.ShopUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/13 16:01
 * @Version 1.0
 */
@Data
public class WeChatRegisterDto implements Serializable {

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * openId
     */
    private String openId;

    /**
     * 转换成会员对象
     * @return
     */
    public ShopUser toShopUser() {
        ShopUser shopUser = new ShopUser();
        shopUser.setHeader(avatarUrl);
        shopUser.setGender(gender);
        shopUser.setNickname(nickName);
        shopUser.setOpenId(openId);
        return shopUser;
    }

}
