package com.lt.dto;

import com.lt.pojo.ShopProduct;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/2 22:08
 * @Version 1.0
 */
@Data
public class ShopPackDto implements Serializable {

    /**
     * 编号，雪花算法
     */
    private Long id;

    /**
     * 套装名称
     */
    @NotBlank(message = "套装名称不能为空")
    private String name;

    /**
     * 商品列表
     */
    private List<ShopProduct> productList;

}
