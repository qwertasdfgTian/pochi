package com.lt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录token返回视图类
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 19:34
 * @Version 1.0
 */
@Data
public class TokenVo implements Serializable {

    /**
     * 登录时返回的token
     */
    private Serializable token;

    public TokenVo(Serializable token) {
        this.token = token;
    }
}
