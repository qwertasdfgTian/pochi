package com.lt.config.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Author: Mr.Tian
 * @Date: 2020/12/12 16:52
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatConfig {

    private String appId;

    private String appSecret;

    private String loginUrl;

    private String accessTokenUrl;

    private String pushUrl;

    /**
     * 根据code获取登录地址
     *
     * @param code
     * @return
     */
    public String getAuthUrl(String code) {
        return "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=anthorization_code";
    }

    /**
     * 获取AccessToken的请求地址
     *
     * @return
     */
    public String resolveAccessTokenUrl() {
        return accessTokenUrl + "appid=" + appId + "&secret=" + appSecret;
    }

    /**
     * 获取推送接口地址
     *
     * @param accessToken
     * @return
     */
    public String resolvePushUrl(String accessToken) {
        return pushUrl + accessToken;
    }

}
