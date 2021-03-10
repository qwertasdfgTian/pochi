package com.lt.config.wechat;

import com.alibaba.fastjson.JSON;
import com.lt.constant.CoreConstant;
import com.lt.dto.OrderReturnTemplateDto;
import com.lt.dto.OrderTemplateDto;
import com.lt.dto.PushDto;
import com.lt.exception.PochiException;
import com.lt.lock.RedisLock;
import com.lt.service.WxService;
import com.lt.utils.HttpUtils;
import com.lt.vo.WeChatAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 21:54
 * @Version 1.0
 */
@Service
@Slf4j
public class WxServiceImpl implements WxService {

    @Autowired
    private WeChatConfig weChatConfig;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisLock redisLock;

    @Override
    public WeChatAccessToken getAccessToken() {
        // 先从redis里查询
        boolean lock = redisLock.tryLock(CoreConstant.ACCESS_TOKEN_KEY + "lock", 5000L, 5000L);
        if (lock) {
            Long expire = redisTemplate.boundValueOps(CoreConstant.ACCESS_TOKEN_KEY).getExpire();
            if (expire == null || expire < 0) {
                // redis中不存在，需要获取
                try {
                    String body = HttpUtils.get(weChatConfig.resolveAccessTokenUrl()).body();
                    // 拿到了token字符串
                    WeChatAccessToken token = JSON.parseObject(body, WeChatAccessToken.class);
                    redisTemplate.boundValueOps(CoreConstant.ACCESS_TOKEN_KEY).set(body, Integer.parseInt(token.getExpiresIn()) / 2, TimeUnit.MINUTES);
                    return token;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String token = redisTemplate.boundValueOps(CoreConstant.ACCESS_TOKEN_KEY).get();
                return JSON.parseObject(token, WeChatAccessToken.class);
            }
        } else {
            throw new PochiException("获取AccessToken失败");
        }
        return null;
    }

    @Override
    public void pushMessage(Object template, String templateId, String openId) {
        PushDto<Object> pushDto = new PushDto<>();
        pushDto.setAccessToken(getAccessToken().getAccessToken());
        pushDto.setTouser(openId);
        pushDto.setData(template);
        pushDto.setTemplateId(templateId);
        try {
            String body = HttpUtils.post(weChatConfig.resolvePushUrl(pushDto.getAccessToken()), JSON.toJSONString(pushDto)).body();
            log.info("消息推送接口调用成功：{}", body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
