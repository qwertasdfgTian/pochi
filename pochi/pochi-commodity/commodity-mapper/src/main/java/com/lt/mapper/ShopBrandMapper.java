package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopBrand;

public interface ShopBrandMapper extends BaseMapper<ShopBrand> {
    /**
     * 添加
     * @param shopBrand
     */
    void save(ShopBrand shopBrand);
}
