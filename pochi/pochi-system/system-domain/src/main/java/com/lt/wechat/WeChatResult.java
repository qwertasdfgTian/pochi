package com.lt.wechat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/12 16:55
 * @Version 1.0
 */
@Data
public class WeChatResult implements Serializable {

    @JSONField(name = "openid")
    private String openId;

    @JSONField(name = "session_key")
    private String sessionKey;

    @JSONField(name = "unionid")
    private String unionId;

    @JSONField(name = "errcode")
    private String errCode;

    @JSONField(name = "errmsg")
    private String errMsg;

}
