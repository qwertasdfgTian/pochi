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
public class OrderReturnTemplateDto implements Serializable {

    @JSONField(name = "character_string3")
    private TemplateValue orderId;

    @JSONField(name = "date8")
    private TemplateValue orderTime;

    @JSONField(name = "phrase14")
    private TemplateValue returnFS;

    @JSONField(name = "amount13")
    private TemplateValue amount;

    @JSONField(name = "thing1")
    private TemplateValue address;

}
