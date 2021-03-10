package com.lt.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthorizingRealm;

/**
 * 开闭原则
 * @Author: Mr.Tian
 * @Date: 2020/12/10 22:23
 * @Version 1.0
 */
public class UserToken extends UsernamePasswordToken {

    /**
     * 用户类型
     */
    private Class<? extends AuthorizingRealm> userType;

    public UserToken(String username, String password, Class<? extends AuthorizingRealm> userType) {
        // 重写UsernamePasswordToken的方法，加入用户类型 userType
        super(username, password);
        this.userType = userType;
    }

    public Class<? extends AuthorizingRealm> getUserType() {
        return userType;
    }

    public void setUserType(Class<? extends AuthorizingRealm> userType) {
        this.userType = userType;
    }
}
