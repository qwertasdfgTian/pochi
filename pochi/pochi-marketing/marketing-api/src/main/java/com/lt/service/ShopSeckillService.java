package com.lt.service;

import com.lt.dto.ShopSeckillDto;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopSeckill;
import com.lt.vo.*;

import java.text.ParseException;
import java.util.List;

public interface ShopSeckillService {

    /**
     * 添加
     * @param seckillDto
     */
    void save(ShopSeckillDto seckillDto, LoginUser loginUser);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 下架
     * @param id
     */
    void down(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopSeckill> getByPage(Page<ShopSeckill> page);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ShopSeckill get(Long id);

    /**
     * 更新秒杀过期的订单
     * @return
     */
    void updateSecKillStatus();

    /**
     * 查询所有没有开始或者是正在开始的秒杀
     * @return
     */
    List<ShopSeckill> getAll();

    /**
     * 查询是否是秒杀商品
     * @return
     */
    ProductSecKillVo getSecKill(Long id) throws ParseException;

    /**
     * 扣减库存
     * @return
     */
    void updateStock(Long id);

}
