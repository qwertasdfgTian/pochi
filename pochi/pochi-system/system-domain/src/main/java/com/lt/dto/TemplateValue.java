package com.lt.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 23:08
 * @Version 1.0
 */
@Data
public class TemplateValue implements Serializable {

    private String value;

    public TemplateValue() {
    }

    public TemplateValue(String value) {
        this.value = value;
    }
}
