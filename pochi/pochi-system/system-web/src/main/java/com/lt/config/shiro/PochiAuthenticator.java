package com.lt.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/10 22:26
 * @Version 1.0
 */
// 重写doAuthenticate方法来实现根据自己的类型实现对应的realm
    // shiro是将所有的realm全部执行一遍显然是不符合要求的
@Slf4j
public class PochiAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms获取的是否为空
        this.assertRealmsConfigured();
        // 强制转换为我们自定义的token
        UserToken userToken = (UserToken) authenticationToken;
        // 获取登录类型
        Class<? extends AuthorizingRealm> realmClass = userToken.getUserType();
        // 拿到所有realm
        Collection<Realm> realms = this.getRealms();
        // 获取登录类型所对应的realm
        Collection<Realm> typeRealms = new ArrayList<>(2);
        // 遍历realm，找到所有符合条件的realm
        for (Realm realm : realms) {
            if (realm.getClass() == realmClass) {
                log.info("当前realm为：{}", realm.getName());
                typeRealms.add(realm);
            }
        }
        // 判断是单realm还是多realm
        if (typeRealms.size() == 1) {
            return doSingleRealmAuthentication(typeRealms.iterator().next(), userToken);
        } else {
            return doMultiRealmAuthentication(typeRealms, userToken);
        }
    }
}
