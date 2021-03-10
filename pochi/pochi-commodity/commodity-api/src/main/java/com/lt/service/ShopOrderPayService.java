package com.lt.service;

import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.pojo.ShopOrderPay;

import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/19 23:51
 * @Version 1.0
 */
public interface ShopOrderPayService {
    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<ShopOrderPay> getByPage(Page<ShopOrderPay> page);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    ShopOrderPay get(Long id);

    /**
     * 根据商品订单ID查询
     * @param id
     * @return
     */
    ShopOrderPay getByOrderId(Long id);

    /**
     * 根据id删除
     * @Param: id
    */
    void delete(Long id);

    /**
     * 根据创建人查询本月累计消费
     * @Param: loginUser
    */
    BigDecimal getConsumption(LoginUser loginUser);
}
