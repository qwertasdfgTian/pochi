package com.lt.vo;

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
public class ProductSecKillVo implements Serializable {

    // 距离开始的时间
    private Long beginSecKillTime;

    // 距离结束的时间
    private Long endSecKillTime;

    // 秒杀信息
    private ShopSeckill shopSeckill;
}
