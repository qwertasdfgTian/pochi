package com.lt.controller.system;

import com.alibaba.fastjson.JSON;
import com.lt.dto.ShopUserBindDto;
import com.lt.dto.ShopUserLoginDto;
import com.lt.dto.WeChatRegisterDto;
import com.lt.vo.Result;
import com.lt.config.wechat.WeChatConfig;
import com.lt.enums.ResultEnums;
import com.lt.pojo.ShopUser;
import com.lt.wechat.WeChatResult;
import com.lt.vo.TokenVo;
import com.lt.service.ShopUserService;
import com.lt.vo.LoginUser;
import com.lt.config.shiro.ShopUserRealm;
import com.lt.config.shiro.UserToken;
import com.lt.utils.HttpUtils;
import com.lt.utils.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

/**
 * 微信相关controller
 *
 * @Author: 杨德石
 * @Date: 2020/12/12 17:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/wx")
public class WeChatController {

    @Autowired
    private ShopUserService shopUserService;
    @Autowired
    private WeChatConfig weChatConfig;

    /**
     * 微信登录
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/wxLogin/{code}", method = RequestMethod.GET)
    public Result<?> wxLogin(@PathVariable String code) throws Exception {
        // 发送请求到微信服务器
        String loginBody = HttpUtils.get(weChatConfig.getAuthUrl(code)).body();
        // 转换成WeChatResult
        WeChatResult weChatResult = JSON.parseObject(loginBody, WeChatResult.class);
        // shiro登录
        Subject subject = SecurityUtils.getSubject();
        // 我们约定，openid为username，unionid为password
        AuthenticationToken authenticationToken = new UserToken(weChatResult.getOpenId(), weChatResult.getOpenId(), ShopUserRealm.class);
        try {
            subject.login(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(weChatResult);
        }
        // 获取sessionId
        Serializable token = subject.getSession().getId();
        return new Result<>(new TokenVo(token));
    }

    /**
     * 手机号登录
     *
     * @param shopUserLoginDto
     * @return
     */
    @RequestMapping(value = "/wxLoginByPhone", method = RequestMethod.POST)
    public Result<?> wxLoginByPhone(@RequestBody ShopUserLoginDto shopUserLoginDto) throws Exception {
        ShopUser user = this.shopUserService.getByPhone(shopUserLoginDto.getPhone());
        if(user==null){
            return new Result<>(ResultEnums.USER_NOT_FOUND);
        }
        if(!user.getPassword().equals(shopUserLoginDto.getPassword()) && user!=null) {
            return new Result<>(ResultEnums.LOGIN_ERROR);
        }
        // shiro登录
        Subject subject = SecurityUtils.getSubject();
        // 我们约定，openid为username，unionid为password
        AuthenticationToken authenticationToken = new UserToken(user.getOpenId(), user.getOpenId(), ShopUserRealm.class);
        try {
            subject.login(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(ResultEnums.LOGIN_ERROR);
        }
        // 获取sessionId
        Serializable token = subject.getSession().getId();
        return new Result<>(new TokenVo(token));
    }

    /**
     * 注册登录
     *
     * @param weChatRegisterDto
     * @return
     */
    @RequestMapping(value = "/registerLogin", method = RequestMethod.POST)
    public Result<?> registerLogin(@RequestBody WeChatRegisterDto weChatRegisterDto) {
        ShopUser user = this.shopUserService.getByOpenId(weChatRegisterDto.getOpenId());
        if(user==null) {
            // 注册
            this.shopUserService.register(weChatRegisterDto.toShopUser());
        }
        //  剩下的逻辑和登录一模一样
        // shiro登录
        Subject subject = SecurityUtils.getSubject();
        // 我们约定，openid为username，unionid为password
        AuthenticationToken authenticationToken = new UserToken(weChatRegisterDto.getOpenId(), weChatRegisterDto.getOpenId(), ShopUserRealm.class);
        try {
            subject.login(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(ResultEnums.LOGIN_ERROR);
        }
        // 获取sessionId
        Serializable token = subject.getSession().getId();
        return new Result<>(new TokenVo(token));
    }

    /**
     * 绑定用户
     *
     * @param shopUserBindDto
     * @return
     */
    @RequestMapping(value = "/bindUser", method = RequestMethod.POST)
    public Result<?> bindUser(@RequestBody ShopUserBindDto shopUserBindDto) {
        ShopUser shopUser = this.shopUserService.bindUser(shopUserBindDto);
        // 转换成登录用户
        LoginUser loginUser = shopUser.toLoginUser();
        // 更新当前登录用户
        ShiroUtils.setUser(loginUser);
        return new Result<>("绑定成功");
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result<LoginUser> info() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        return new Result<>(loginUser);
    }
}
