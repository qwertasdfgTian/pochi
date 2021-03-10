package com.lt.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.OrderDto;
import com.lt.dto.OrderProductDto;
import com.lt.enums.OperateEnum;
import com.lt.enums.OrderStateEnum;
import com.lt.enums.StateEnums;
import com.lt.mapper.*;
import com.lt.pojo.*;
import com.lt.service.ShopOrderService;
import com.lt.utils.DateUtils;
import com.lt.utils.HttpUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service(methods = {@Method(name = "createOrder", retries = 0)})
@Slf4j
public class ShopOrderServiceImpl implements ShopOrderService{

    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private ShopCouponMapper shopCouponMapper;
    @Autowired
    private ShopUserAddressMapper shopUserAddressMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    @Autowired
    private ShopOrderItemMapper shopOrderItemMapper;
    @Autowired
    private ShopOrderHistoryMapper shopOrderHistoryMapper;
    @Autowired
    private ShopOrderPayMapper shopOrderPayMapper;
    @Autowired
    private ShopUserMapper shopUserMapper;
    @Autowired
    private ShopUserStatisticMapper shopUserStatisticMapper;
    @Autowired
    private ShopCartItemMapper shopCartItemMapper;
    @Autowired
    private ShopCouponHistoryMapper shopCouponHistoryMapper;
    @Autowired
    private ShopOrderReturnApplyMapper shopOrderReturnApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopOrder createOrder(OrderDto orderDto, LoginUser loginUser) {
        // 1. 前端提交订单参数：收货地址编号、商品编号、购买数量、优惠券编号等
        List<OrderProductDto> productDtoList = orderDto.getProductList();
        // 查询商品集合
        List<Long> productIds = productDtoList.stream().map(OrderProductDto::getProductId).collect(Collectors.toList());
        List<ShopProduct> productList = this.shopProductMapper.selectBatchIds(productIds);
        Long couponId = orderDto.getCouponId();
        Long addressId = orderDto.getAddressId();
        // 查询地址
        ShopUserAddress address = this.shopUserAddressMapper.selectById(addressId);
        // 2. 商品扣减库存
        for (OrderProductDto product : productDtoList) {
            ShopProduct shopProduct=new ShopProduct();
            shopProduct.setId(product.getProductId());
            shopProduct.setStock(product.getCount());
            this.shopProductMapper.updateById(shopProduct);
        }
        // 3. 计算总金额
        BigDecimal totalMoney = BigDecimal.ZERO;
        // 总积分
        BigDecimal totalPoint = BigDecimal.ZERO;
        for (ShopProduct product : productList) {
            OrderProductDto temp = productDtoList.stream().filter(d -> d.getProductId().equals(product.getId())).findFirst().orElse(null);
            product.setStock(product.getStock() - temp.getCount());
            // 计算该商品需要支付金额
            BigDecimal productMoney = product.getPrice().multiply(new BigDecimal(temp.getCount()));
            // 计算总金额
            totalMoney = totalMoney.add(productMoney);
            // 计算总积分
            BigDecimal point = product.getPoint();
            if (point != null) {
                totalPoint = totalPoint.add(point);
            }
        }
        // 应付金额
        BigDecimal payAmount = totalMoney;
        // 优惠券抵扣金额
        BigDecimal couponAmount = BigDecimal.ZERO;
        // 如果用了优惠券就扣除优惠券的金额
        if (couponId != null) {
            // 查询优惠券
            ShopCoupon shopCoupon = this.shopCouponMapper.selectById(couponId);
            if (shopCoupon != null) {
                couponAmount = shopCoupon.getAmount();
                payAmount = totalMoney.subtract(shopCoupon.getAmount());
            }
        }
        // 取出创建人
        ShopOrder order = saveOrder(orderDto, productDtoList, productList, address, totalMoney, totalPoint, payAmount, couponAmount, loginUser);
        // 5. 优惠券状态变更为已使用
        if (orderDto.getCouponId() != null) {
            // 更新优惠券使用数量
            ShopCoupon coupon = this.shopCouponMapper.selectById(orderDto.getCouponId());
            coupon.setUseCount(coupon.getUseCount() + 1);
            this.shopCouponMapper.updateById(coupon);
            // 优惠券领取历史表数据状态变更为已使用，更新使用时间和订单号
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopCouponHistory.COL_CREATE_BY,loginUser.getUsername());
            qw.eq(ShopCouponHistory.COL_COUPON_ID,coupon.getId());
            ShopCouponHistory shopCouponHistory = this.shopCouponHistoryMapper.selectOne(qw);
            shopCouponHistory.setUseStatus(StateEnums.COUPON_USED.getCode());
            shopCouponHistory.setUseTime(DateUtils.newDateTime());
            shopCouponHistory.setOrderId(order.getId());
            this.shopCouponHistoryMapper.updateById(shopCouponHistory);
        }
        // 6. 如果购物车项ID不为空，清空购物车项
        Long cartId = productDtoList.get(0).getCartId();
        if (cartId != null) {
            // 说明是购物车下单，清空这些购物车项
            List<Long> cartIds = productDtoList.stream().map(OrderProductDto::getCartId).collect(Collectors.toList());
            this.shopCartItemMapper.deleteBatchIds(cartIds);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPayOrder(ShopOrder order,LoginUser loginUser,String outTradeNo) throws Exception {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,order.getId());
        if(this.shopOrderPayMapper.selectOne(qw)!=null){
            this.shopOrderPayMapper.delete(qw);
        }
        // 入库
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setId(idWorker.nextId());
        orderPay.setCreateBy(loginUser.getUsername());
        orderPay.setOutTradeNo(outTradeNo);
        orderPay.setOrderId(order.getId());
        orderPay.setPayAmount(order.getPayAmount());
        this.shopOrderPayMapper.insert(orderPay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopOrder handleOrderNotify(String orderId,String trade_no) {
        // 查询支付订单
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = shopOrderPayMapper.selectOne(qw);
        log.info("查询支付订单，支付订单号为：{}，订单详情：{}", trade_no, JSON.toJSONString(shopOrderPay));
        // 查询商品订单
        ShopOrder order = this.shopOrderMapper.selectById(orderId);
        log.info("查询商品订单，订单号为：{}，订单详情：{}", orderId, JSON.toJSONString(order));
        // 1. 修改商品订单
        order.setStatus(OrderStateEnum.WAIT_SEND.getCode());
        order.setPaymentTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(order);
        // 2. 添加订单历史状态
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.SYSTEM.getType());
        orderHistory.setOrderStatus(OrderStateEnum.WAIT_SEND.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
        // 3. 用户积分增加
        String integration = order.getIntegration();
        String username = order.getCreateBy();
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopUser.COL_PHONE,username);
        ShopUser user = this.shopUserMapper.selectOne(qw1);
        if (StringUtils.isNotBlank(integration)) {
            BigDecimal point = new BigDecimal(integration);
            user.setPoint(user.getPoint().add(point));
            this.shopUserMapper.updateById(user);
        }
        // 4. 用户统计信息增加
        QueryWrapper qw2=new QueryWrapper();
        qw2.eq(ShopUserStatistic.COL_USER_ID,user.getId());
        ShopUserStatistic shopUserStatistic = this.shopUserStatisticMapper.selectOne(qw2);
        shopUserStatistic.setOrderCount(shopUserStatistic.getOrderCount() + 1);
        shopUserStatistic.setConsumeAmount(shopUserStatistic.getConsumeAmount().add(order.getPayAmount()));
        if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
            shopUserStatistic.setCouponCount(shopUserStatistic.getCouponCount() - 1);
        }
        this.shopUserStatisticMapper.updateById(shopUserStatistic);
        // 5. 支付订单状态变更
        shopOrderPay.setOrderNo(trade_no);
        shopOrderPay.setStatus(OrderStateEnum.PAY_SUCCESS.getCode());
        this.shopOrderPayMapper.updateById(shopOrderPay);
        // 设置openid方便消息推送
        order.setOpenId(user.getOpenId());
        return order;
    }

    @Override
    public Page<ShopOrderVo> getMyOrder(Page<ShopOrderVo> page,LoginUser loginUser) {
        // 分页查询当前登录用户的订单列表
        page.getParams().put("createBy", loginUser.getUsername());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopOrder> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopOrder> qw=new QueryWrapper<>();
        ShopOrder shopOrder=new ShopOrder();
        BeanUtil.copyProperties(page.getParams(),shopOrder);
        qw.eq(ShopOrder.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(null!=shopOrder.getStatus(),ShopOrder.COL_STATUS,shopOrder.getStatus());
        qw.eq(StringUtils.isNotBlank(shopOrder.getCreateBy()),ShopOrder.COL_CREATE_BY,shopOrder.getCreateBy());
        qw.orderByDesc(ShopOrder.COL_UPDATE_TIME);
        this.shopOrderMapper.selectPage(pages,qw);
        List<ShopOrder> list = pages.getRecords();
        // 取出订单ID集合，查询每个订单的订单项信息
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> orderIds = list.stream().map(ShopOrder::getId).collect(Collectors.toList());
            QueryWrapper qw1=new QueryWrapper();
            qw1.in(ShopOrderItem.COL_ORDER_ID,orderIds);
            List<ShopOrderItem> itemList = this.shopOrderItemMapper.selectList(qw1);
            List<ShopOrderVo> orderVoList = list.stream().map(o -> {
                ShopOrderVo vo = new ShopOrderVo();
                BeanUtils.copyProperties(o, vo);
                List<ShopOrderItem> orderItems = itemList.stream().filter(i -> i.getOrderId().equals(o.getId())).collect(Collectors.toList());
                vo.setItemList(orderItems);
                return vo;
            }).collect(Collectors.toList());
            page.setList(orderVoList);
        } else {
            page.setList(new ArrayList<>(0));
        }
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public ShopOrderVo get(Long id) {
        // 根据id查询订单
        ShopOrder order = this.shopOrderMapper.selectById(id);
        ShopOrderVo vo = new ShopOrderVo();
        BeanUtils.copyProperties(order, vo);
        // 根据订单id查询订单项
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,id);
        List<ShopOrderItem> itemList = this.shopOrderItemMapper.selectList(qw);
        vo.setItemList(itemList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveById(Long id) {
        ShopOrder order = this.shopOrderMapper.selectById(id);
        order.setStatus(OrderStateEnum.ALREADY_SIGN.getCode());
        order.setReceiveTime(DateUtils.newDateTime());
        order.setConfirmStatus(1);
        this.shopOrderMapper.updateById(order);
        // 存储订单历史
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.ALREADY_SIGN.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
    }

    @Override
    public Page<ShopOrder> getByPage(Page<ShopOrder> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopOrder> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopOrder> qw=new QueryWrapper<>();
        ShopOrder shopOrder=new ShopOrder();
        BeanUtil.copyProperties(page.getParams(),shopOrder);
        //qw.eq(ShopOrder.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(null!=shopOrder.getId(),ShopOrder.COL_ID,shopOrder.getId());
        qw.eq(null!=shopOrder.getStatus(),ShopOrder.COL_STATUS,shopOrder.getStatus());
        qw.eq(StringUtils.isNotBlank(shopOrder.getCreateBy()),ShopOrder.COL_CREATE_BY,shopOrder.getCreateBy());
        qw.orderByDesc(ShopOrder.COL_CREATE_TIME);
        this.shopOrderMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public void changeOrderStatus(ShopOrder order) {
        if(order.getStatus()==OrderStateEnum.ALREADY_SEND.getCode()){
            order.setDeliveryTime(DateUtils.newDateTime());
        }
        order.setUpdateTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(order);
        // 存储订单历史
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(order.getStatus());
        this.shopOrderHistoryMapper.insert(orderHistory);
    }

    @Override
    public List<ShopOrderHistory> getHistory(Long id) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderHistory.COL_ORDER_ID,id);
        return this.shopOrderHistoryMapper.selectList(qw);
    }

    @Override
    public ShopOrder getById(Long id) {
        return this.shopOrderMapper.selectById(id);
    }

    @Override
    public List<OrderMonthVo> monthOrder() {
        return this.shopOrderMapper.monthOrder();
    }

    @Override
    public List<OrderPointVo> orderPoint() {
        List<OrderPointVo> list = this.shopOrderMapper.orderPoint();
        for (OrderPointVo vo : list) {
            vo.setStatusMsg(OrderStateEnum.getStatusByCode(vo.getStatus()).getMsg());
        }
        return list;
    }

    @Override
    public ShopOrderPay queryOrderPayOrderId(String orderId) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        return this.shopOrderPayMapper.selectOne(qw);
    }

    @Override
    public void failOrderPay(String orderId) {
        // 查询支付订单
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = shopOrderPayMapper.selectOne(qw);
        // 支付订单状态变更
        shopOrderPay.setStatus(OrderStateEnum.PAY_FAIL.getCode());
        this.shopOrderPayMapper.updateById(shopOrderPay);
    }

    @Override
    public OrderTypeNumVo getOrderTypeNum(LoginUser loginUser) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrder.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.eq(ShopOrder.COL_CREATE_BY,loginUser.getUsername());
        List<ShopOrder> shopOrders = this.shopOrderMapper.selectList(qw);
        Integer waitpaynum =0;
        Integer waitsendnum =0;
        Integer alreadysendnum =0;
        Integer alreadysignnum =0;
        for (ShopOrder shopOrder : shopOrders) {
            switch(shopOrder.getStatus()){
                case 0:
                    waitpaynum += 1;break;
                case 2:
                    waitsendnum += 1;break;
                case 3:
                    alreadysendnum += 1;break;
                case 4:
                    alreadysignnum += 1;break;
            }
        }
        OrderTypeNumVo orderTypeNumVo=new OrderTypeNumVo(waitpaynum,waitsendnum,alreadysendnum,alreadysignnum);
        return orderTypeNumVo;
    }

    @Override
    public void deleteOrderById(Long orderId) {
        // 删除订单
        ShopOrder shopOrder=new ShopOrder();
        shopOrder.setId(orderId);
        shopOrder.setDeleted(StateEnums.DELETED.getCode());
        this.shopOrderMapper.updateById(shopOrder);
        // 删除订单详情
        ShopOrderItem shopOrderItem=new ShopOrderItem();
        shopOrderItem.setDeleted(StateEnums.DELETED.getCode());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,orderId);
        this.shopOrderItemMapper.update(shopOrderItem,qw);
    }

    @Override
    public ShopOrderPay cancelOrderById(Long orderId,LoginUser loginUser) {
        // 查询订单
        ShopOrder shopOrder1 = this.shopOrderMapper.selectById(orderId);
        // 查询订单详情
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,orderId);
        List<ShopOrderItem> shopOrderItems = this.shopOrderItemMapper.selectList(qw);
        // 查询支付订单详情
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = this.shopOrderPayMapper.selectOne(qw1);
        // 更改订单状态
        ShopOrder shopOrder=new ShopOrder();
        shopOrder.setId(orderId);
        shopOrder.setStatus(OrderStateEnum.INVALID.getCode());
        shopOrder.setUpdateTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(shopOrder);
        // 存储订单历史
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(orderId);
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.INVALID.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
        // 退款,如果不是代付款就进行退款
        if(shopOrder1.getStatus()!=0){
            //保存退费订单
            for (ShopOrderItem shopOrderItem : shopOrderItems) {
                ShopOrderReturnApply shopOrderReturnApply = new ShopOrderReturnApply();
                shopOrderReturnApply.setId(idWorker.nextId());
                shopOrderReturnApply.setOrderId(orderId);
                shopOrderReturnApply.setProductId(shopOrderItem.getProductId());
                shopOrderReturnApply.setCreateTime(DateUtils.newDateTime());
                shopOrderReturnApply.setReturnAmount(shopOrder1.getPayAmount());
                shopOrderReturnApply.setMemberUsername(loginUser.getUsername());
                shopOrderReturnApply.setReturnName(shopOrder1.getReceiverName());
                shopOrderReturnApply.setReturnPhone(shopOrder1.getReceiverPhone());
                shopOrderReturnApply.setStatus(OrderStateEnum.FINISH.getCode());
                shopOrderReturnApply.setHandleTime(DateUtils.newDateTime());
                shopOrderReturnApply.setProductPic(shopOrderItem.getProductPic());
                shopOrderReturnApply.setProductName(shopOrderItem.getProductName());
                shopOrderReturnApply.setProductBrand(shopOrderItem.getProductBrand());
                shopOrderReturnApply.setProductCount(shopOrderItem.getProductQuantity());
                shopOrderReturnApply.setProductPrice(shopOrderItem.getProductPrice());
                shopOrderReturnApply.setReason("我不想要了");
                shopOrderReturnApply.setHandleMan(OperateEnum.SYSTEM.getType());
                shopOrderReturnApply.setReceiveMan(shopOrder1.getReceiverName());
                this.shopOrderReturnApplyMapper.insert(shopOrderReturnApply);
            }
            return shopOrderPay;
        }
        return null;
    }

    /**
     * 保存订单
     *
     * @param orderDto       前端传递的参数
     * @param productDtoList 商品id和数量
     * @param productList    商品集合
     * @param address        地址
     * @param totalMoney     总金额
     * @param totalPoint     总积分
     * @param payAmount      应付金额
     * @param couponAmount   优惠券减免金额
     * @param loginUser      登录人
     * @return
     */
    private ShopOrder saveOrder(OrderDto orderDto, List<OrderProductDto> productDtoList, List<ShopProduct> productList, ShopUserAddress address, BigDecimal totalMoney, BigDecimal totalPoint, BigDecimal payAmount, BigDecimal couponAmount, LoginUser loginUser) {
        // 4. 存入订单表、订单项表、订单历史表
        ShopOrder order = new ShopOrder();
        order.setId(idWorker.nextId());
        order.setCreateBy(loginUser.getUsername());
        order.setTotalAmount(totalMoney);
        order.setPayAmount(payAmount);
        order.setCouponAmount(couponAmount);
        order.setDeliveryCompany("菜鸟快递");
        order.setDeliverySn(idWorker.nextId() + "");
        order.setIntegration(totalPoint.toString());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode("061200");
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setNote(orderDto.getNote());
        // order.setCouponAmount(BigDecimal.ZERO);

        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.WAIT_PAY.getCode());
        // 构造订单项
        BigDecimal money=BigDecimal.ZERO;
        List<ShopOrderItem> shopOrderItemList = productList.stream().map(p -> {
            money.add(p.getTransFee());
            ShopOrderItem item = new ShopOrderItem();
            item.setId(idWorker.nextId());
            item.setOrderId(order.getId());
            item.setProductId(p.getId());
            item.setProductPic(p.getPic());
            item.setProductName(p.getName());
            item.setProductBrand(p.getBrandName());
            item.setProductPrice(p.getPrice());
            OrderProductDto temp = productDtoList.stream().filter(d -> d.getProductId().equals(p.getId())).findFirst().orElse(null);
            item.setProductQuantity(temp.getCount());
            item.setProductCategoryId(p.getCategoryId());
            BigDecimal point = p.getPoint();
            if (point == null) {
                point = BigDecimal.ZERO;
            }
            item.setIntegration(point.intValue());
            return item;
        }).collect(Collectors.toList());
        // 订单运费和订单加上运费的费用
        order.setFreightAmount(money);
        order.setPayAmount(money.add(payAmount));
        // 订单、订单项、订单历史入库，商品库存扣减
        this.shopOrderMapper.insert(order);
        this.shopOrderHistoryMapper.insert(orderHistory);
        for (ShopOrderItem shopOrderItem : shopOrderItemList) {
            this.shopOrderItemMapper.insert(shopOrderItem);
        }
        for (ShopProduct product : productList) {
            ShopProduct shopProduct =new ShopProduct();
            shopProduct.setId(product.getId());
            shopProduct.setStock(product.getStock());
            this.shopProductMapper.updateById(shopProduct);
        }
        return order;
    }
}
