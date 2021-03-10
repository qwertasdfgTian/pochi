package com.lt.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/13 17:03
 * @Version 1.0
 */
@Data
public class ShopUserBindDto implements Serializable {

    private String phone;

    private String password;

    /**
     * 绑定类型，1绑定现有账户 ，2绑定新账户
     */
    private Integer bindType;

}
