package com.lt.config.shiro;

import com.lt.enums.ResultEnums;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.mapper.SysMenuMapper;
import com.lt.pojo.SysUser;
import com.lt.service.SysUserService;
import com.lt.vo.LoginUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

/**
 * 系统用户登录realm
 */
@Component("sysUserRealm")
public class SysUserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy //由于shiro的容器和spring的冲突
    private SysUserService sysUserService;
    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 授权方法
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取登录用户
        LoginUser sysUserVo = (LoginUser) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(new HashSet<>(sysUserVo.getAuths()));
        return info;
    }

    /**
     * 认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 处理登录逻辑
        UserToken userToken = (UserToken) token;
        String username = userToken.getUsername();
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new PochiException(ResultEnums.LOGIN_PARAM_ERROR);
        }
        if (StateEnums.NOT_ENABLE.getCode().equals(sysUser.getStatus())) {
            // 未启用用户
            throw new PochiException(ResultEnums.LOGIN_PARAM_ERROR);
        }
        if (StateEnums.DELETED.getCode().equals(sysUser.getDeleted())) {
            // 已删除用户
            throw new PochiException(ResultEnums.LOGIN_PARAM_ERROR);
        }
        // 创建SYsUserVo拷贝属性
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        // 在这里查询权限
        List<String> auths = this.sysMenuMapper.getMenuCodeByUserId(sysUser.getId());
        if (CollectionUtils.isEmpty(auths)) {
            throw new PochiException("当前用户不具备任何权限，禁止登录");
        }
        loginUser.setAuths(auths);
        return new SimpleAuthenticationInfo(loginUser, sysUser.getPassword(), this.getName());
    }
}
