package com.lt.controller;

import com.lt.enums.ResultEnums;
import com.lt.vo.Result;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;

/**
 * @Author: Mr.Tian
 */
@DefaultProperties(defaultFallback = "fallback")
public class BaseController {

    /**
     * 如远程服务不可用，或者出现异常，回调的方法
     * @return
     */
    public Result<?> fallback(){
        return new Result<>(ResultEnums.SERVER_ERROR);
    }
}
