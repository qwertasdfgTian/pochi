package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.dto.OrderProductDto;
import com.lt.pojo.ShopOrder;
import com.lt.vo.OrderMonthVo;
import com.lt.vo.OrderPointVo;

import java.util.List;

public interface ShopOrderMapper extends BaseMapper<ShopOrder> {

    /**
     * 查询当前月份的订单
    */
    List<OrderMonthVo> monthOrder();

    /**
     * 查询每种订单状态的数量
    */
    List<OrderPointVo> orderPoint();
}