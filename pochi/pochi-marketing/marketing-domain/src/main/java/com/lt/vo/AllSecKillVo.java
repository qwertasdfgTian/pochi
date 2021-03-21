package com.lt.vo;

import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopSeckill;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-02-27 20:54
 **/
@Data
public class AllSecKillVo implements Serializable {

    // 下一场秒杀开始的时间
    private Long nextSecKillTime;

    // 秒杀列表
    private List<ShopSeckill> all;
}
