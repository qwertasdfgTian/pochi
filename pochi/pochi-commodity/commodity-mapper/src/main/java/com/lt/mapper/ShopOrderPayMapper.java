package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopOrderPay;

import java.math.BigDecimal;

public interface ShopOrderPayMapper extends BaseMapper<ShopOrderPay> {

    /**
     * 查询本月的消费
     * @Param: username
    */
    BigDecimal getConsumption(String username);
}