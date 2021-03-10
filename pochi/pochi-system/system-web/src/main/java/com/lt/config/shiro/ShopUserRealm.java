package com.lt.config.shiro;

import com.lt.enums.ResultEnums;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.pojo.ShopUser;
import com.lt.service.ShopUserService;
import com.lt.vo.LoginUser;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/10 22:33
 * @Version 1.0
 */
@Component("shopUserRealm")
public class ShopUserRealm extends AuthorizingRealm {

    @Autowired
    private ShopUserService shopUserService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UserToken userToken = (UserToken) authenticationToken;
        // 拿到username，也就是我们的openid
        String openId = userToken.getUsername();
        // 根据openid去查询用户
        ShopUser shopUser = shopUserService.getByOpenId(openId);
        if(shopUser == null) {
            throw new AuthenticationException("用户不存在！");
        }
        if (StateEnums.NOT_ENABLE.getCode().equals(shopUser.getStatus())) {
            // 未启用用户
            throw new PochiException(ResultEnums.LOGIN_PARAM_ERROR);
        }
        if (StateEnums.DELETED.getCode().equals(shopUser.getDeleted())) {
            // 已删除用户
            throw new PochiException(ResultEnums.LOGIN_PARAM_ERROR);
        }
        // 转换成loginuser
        LoginUser loginUser = shopUser.toLoginUser();
        return new SimpleAuthenticationInfo(loginUser, shopUser.getOpenId(), getName());
    }
}
