package com.lt.service;

import com.lt.dto.OrderDto;
import com.lt.pojo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.vo.*;

import java.util.List;

public interface ShopOrderService {

    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    ShopOrder createOrder(OrderDto orderDto, LoginUser loginUser);

    /**
     * 创建支付订单
     * @param order
     * @return
     * @throws Exception
     */
    void createPayOrder(ShopOrder order,LoginUser loginUser,String outTradeNo) throws Exception;

    /**
     * 处理订单支付回调
     */
    ShopOrder handleOrderNotify(String orderId,String trade_no);

    /**
     * 分页查询前台用户订单列表
     * @param page
     * @return
     */
    Page<ShopOrderVo> getMyOrder(Page<ShopOrderVo> page,LoginUser loginUser);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    ShopOrderVo get(Long id);

    /**
     * 根据id收货
     * @param id
     */
    void receiveById(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopOrder> getByPage(Page<ShopOrder> page);

    /**
     * 改变订单状态
     * @param order
     */
    void changeOrderStatus(ShopOrder order);

    /**
     * 查询订单历史
     * @param id
     * @return
     */
    List<ShopOrderHistory> getHistory(Long id);

    /**
     * 根据id查询
     * @Param: id
    */
    ShopOrder getById(Long id);

    /**
     * 月订单统计
     * @return
     */
    List<OrderMonthVo> monthOrder();

    /**
     * 查询每种订单占比
     * @return
     */
    List<OrderPointVo> orderPoint();

    // 查询订单状态
    ShopOrderPay queryOrderPayOrderId(String outTradeNo);

    /**
     * 订单支付失败修改状态
     * @Param: orderId
    */
    void failOrderPay(String orderId);

    /**
     * 根据创建人查询各种订单的数量
     * @return
     */
    OrderTypeNumVo getOrderTypeNum(LoginUser loginUser);

    /**
     * 根据id删除订单
     * @Param: orderId
    */
    void deleteOrderById(Long orderId);

    /**
     * 根据id取消订单
     * @Param: orderId
     */
    ShopOrderPay cancelOrderById(Long orderId,LoginUser loginUser);

    /**
     * 创建秒杀订单
     * @Param: orderId
     */
    ShopOrder createSecKillOrder(ShopSeckill shopSeckill, LoginUser loginUser);

    // 查询详情
    ShopOrderItem selectItem(Long id);
}
