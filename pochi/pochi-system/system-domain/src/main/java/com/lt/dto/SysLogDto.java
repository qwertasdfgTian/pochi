package com.lt.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询是MongoDB的
 * 不需要那么多复杂的维度查询
 * 因此为了代码可读性
 * 使用实体类
 * @Author: Mr.Tian
 * @Date: 2020/11/17 23:55
 * @Version 1.0
 */
@Data
public class SysLogDto implements Serializable {

    /**
     * 请求地址
     */
    private String logUrl;

    /**
     * 请求状态
     */
    private Integer logStatus;

    /**
     * 控制层
     */
    private String logController;

    /**
     * 创建时间
     * Elementui中，传的创建时间是个数组
     */
    private List<String> createdDate;

    /**
     * 页数
     */
    private Integer pageNumber;

    /**
     * 每页条数
     */
    private Integer pageSize;

}
