package com.lt.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/24 23:35
 * @Version 1.0
 */
@Data
public class ShopBrandDto implements Serializable {

    /**
     * 编号
     */
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 排序
     */
    @NotNull(message = "排序编码不能为空")
    private Long sort;

    /**
     * 是否显示
     */
    private Integer showStatus;
    /**
     * logo
     */
    private String logo;
    /**
     * 分类ID集合
     */
    private List<Long> categoryIds;

}
