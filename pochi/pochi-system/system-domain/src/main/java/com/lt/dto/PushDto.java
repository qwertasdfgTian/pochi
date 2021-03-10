package com.lt.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 23:04
 * @Version 1.0
 */
@Data
public class PushDto<T> implements Serializable {

    @JSONField(name = "access_token")
    private String accessToken;

    private String touser;

    @JSONField(name = "template_id")
    private String templateId;

    private T data;

}
