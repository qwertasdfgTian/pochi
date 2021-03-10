package com.lt.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-02-22 20:55
 **/
@Data
public class ShopUserLoginDto implements Serializable {

    // 用户登录的手机号
    private String phone;

    // 用户登录的密码
    private String password;
}
