package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopProductCategory;

import java.util.List;

public interface ShopProductCategoryMapper extends BaseMapper<ShopProductCategory> {

    List<ShopProductCategory> getNavList();

}
