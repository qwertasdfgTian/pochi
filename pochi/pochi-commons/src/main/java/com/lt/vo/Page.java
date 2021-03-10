package com.lt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页类
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:17
 * @Version 1.0
 */
@Data
public class Page<T> implements Serializable {

    /**
     * 当前页数
     */
    private Integer pageNumber;

    /**
     * 每页显示条数
     */
    private Integer pageSize = 20;

    /**
     * 总条数
     */
    private Integer totalCount;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 数据
     */
    private List<T> list;

    /**
     * 接收传参
     */
    private Map<String, Object> params = new HashMap<>(8);

    /**
     * 排序列
     */
    private String sortColumn;

    /**
     * '
     * 排序方式，asc或者desc
     */
    private String sortMethod;

    /**
     * 获取index
     *
     * @return
     */
    public Integer getIndex() {
        return (pageNumber - 1) * pageSize;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        if (this.pageNumber == null || this.pageNumber < 1) {
            this.pageNumber = 1;
        }
    }

    /**
     * 在设置总条数的时候，计算总页数
     *
     * @param totalCount
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil(totalCount * 1.0 / pageSize);
    }

}
