package com.lt.vo;

import com.lt.pojo.ShopBrand;
import com.lt.pojo.ShopProductCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/17 21:24
 * @Version 1.0
 */
@Data
public class CategoryBrandVo implements Serializable {

    private List<ShopProductCategory> categoryList;

    private List<ShopBrand> brandList;

    public CategoryBrandVo() {
    }

    public CategoryBrandVo(List<ShopProductCategory> categoryList, List<ShopBrand> brandList) {
        this.categoryList = categoryList;
        this.brandList = brandList;
    }
}
