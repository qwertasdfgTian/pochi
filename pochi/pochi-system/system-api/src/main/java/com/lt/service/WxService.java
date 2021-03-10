package com.lt.service;

import com.lt.dto.OrderReturnTemplateDto;
import com.lt.dto.OrderTemplateDto;
import com.lt.vo.WeChatAccessToken;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 21:53
 * @Version 1.0
 */
public interface WxService {

    /**
     * 获取微信的accessToken
     *
     * @return
     */
    WeChatAccessToken getAccessToken();

    /**
     * 推送消息
     *
     * @param template
     * @param templateId
     * @param openId
     */
    void pushMessage(Object template, String templateId, String openId);

}
