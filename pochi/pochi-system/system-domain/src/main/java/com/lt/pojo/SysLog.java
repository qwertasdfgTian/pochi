package com.lt.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 系统日志实体
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:43
 * @Version 1.0
 */
@Data
@Document(collection = "sys_log")
public class SysLog implements Serializable {

    /**
     * ID，雪花算法
     */
    @Id
    private String logId;

    /**
     * 请求 地址
     */
    @Field("log_url")
    private String logUrl;

    /**
     * 参数
     */
    @Field("log_params")
    private String logParams;

    /**
     * 1正常0异常
     */
    @Field("log_status")
    private Integer logStatus;

    /**
     * 异常文本
     */
    @Field("log_message")
    private String logMessage;

    /**
     * 浏览器UA表示
     */
    @Field("log_ua")
    private String logUa;

    /**
     * 请求controller
     */
    @Field("log_controller")
    private String logController;

    /**
     * 请求方法
     */
    @Field("log_method")
    private String logMethod;

    /**
     * 响应时间
     */
    @Field("log_time")
    private Long logTime;

    /**
     * 返回值
     */
    @Field("log_result")
    private String logResult;

    /**
     * 请求ip
     */
    @Field("log_ip")
    private String logIp;

    /**
     * 创建时间
     */
    @Field("created_date")
    private String createdDate;

    /**
     * 创建人
     */
    @Field("created_by")
    private String createdBy;

}
