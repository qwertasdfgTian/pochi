package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopSeckill;

public interface ShopSeckillMapper extends BaseMapper<ShopSeckill> {

    // 利用数据库的行锁扣减库存
    void updateStock(Long id);

}