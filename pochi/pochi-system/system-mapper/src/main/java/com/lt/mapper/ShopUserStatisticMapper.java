package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopUserStatistic;

import java.util.List;

public interface ShopUserStatisticMapper extends BaseMapper<ShopUserStatistic> {

    /**
     * 查询前十用户信息
    */
    List<ShopUserStatistic> getTopStatistic();
}