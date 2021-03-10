package com.lt.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 21:52
 * @Version 1.0
 */
@Data
public class WeChatAccessToken implements Serializable {

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "expires_in")
    private String expiresIn;

    @JSONField(name = "err_code")
    private String errCode;

    @JSONField(name = "err_msg")
    private String errMsg;

}
