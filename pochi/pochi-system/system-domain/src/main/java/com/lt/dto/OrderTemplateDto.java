package com.lt.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 23:06
 * @Version 1.0
 */
@Data
public class OrderTemplateDto implements Serializable {

    @JSONField(name = "character_string22")
    private TemplateValue orderId;

    @JSONField(name = "time10")
    private TemplateValue orderTime;

    @JSONField(name = "thing29")
    private TemplateValue productName;

    @JSONField(name = "amount8")
    private TemplateValue amount;

    @JSONField(name = "thing11")
    private TemplateValue address;

}
