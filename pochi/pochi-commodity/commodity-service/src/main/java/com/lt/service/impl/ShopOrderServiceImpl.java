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

@Service(methods = {@Method(name = "createOrder", retries = 0)},timeout = 50000)
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
        // 1. ????????????????????????????????????????????????????????????????????????????????????????????????
        List<OrderProductDto> productDtoList = orderDto.getProductList();
        // ??????????????????
        List<Long> productIds = productDtoList.stream().map(OrderProductDto::getProductId).collect(Collectors.toList());
        List<ShopProduct> productList = this.shopProductMapper.selectBatchIds(productIds);
        Long couponId = orderDto.getCouponId();
        Long addressId = orderDto.getAddressId();
        // ????????????
        ShopUserAddress address = this.shopUserAddressMapper.selectById(addressId);
        // 2. ??????????????????
        for (OrderProductDto product : productDtoList) {
            this.shopProductMapper.updateStock(product);
        }
        // 3. ???????????????
        BigDecimal totalMoney = BigDecimal.ZERO;
        // ?????????
        BigDecimal totalPoint = BigDecimal.ZERO;
        for (ShopProduct product : productList) {
            OrderProductDto temp = productDtoList.stream().filter(d -> d.getProductId().equals(product.getId())).findFirst().orElse(null);
            product.setStock(product.getStock() - temp.getCount());
            // ?????????????????????????????????
            BigDecimal productMoney = product.getPrice().multiply(new BigDecimal(temp.getCount()));
            // ???????????????
            totalMoney = totalMoney.add(productMoney);
            // ???????????????
            BigDecimal point = product.getPoint();
            if (point != null) {
                totalPoint = totalPoint.add(point);
            }
        }
        // ????????????
        BigDecimal payAmount = totalMoney;
        // ?????????????????????
        BigDecimal couponAmount = BigDecimal.ZERO;
        // ????????????????????????????????????????????????
        if (couponId != null) {
            // ???????????????
            ShopCoupon shopCoupon = this.shopCouponMapper.selectById(couponId);
            if (shopCoupon != null) {
                couponAmount = shopCoupon.getAmount();
                payAmount = totalMoney.subtract(shopCoupon.getAmount());
            }
        }
        // ???????????????
        ShopOrder order = saveOrder(orderDto, productDtoList, productList, address, totalMoney, totalPoint, payAmount, couponAmount, loginUser);
        // 5. ?????????????????????????????????
        if (orderDto.getCouponId() != null) {
            // ???????????????????????????
            ShopCoupon coupon = this.shopCouponMapper.selectById(orderDto.getCouponId());
            coupon.setUseCount(coupon.getUseCount() + 1);
            this.shopCouponMapper.updateById(coupon);
            // ???????????????????????????????????????????????????????????????????????????????????????
            QueryWrapper qw=new QueryWrapper();
            qw.eq(ShopCouponHistory.COL_CREATE_BY,loginUser.getUsername());
            qw.eq(ShopCouponHistory.COL_COUPON_ID,coupon.getId());
            ShopCouponHistory shopCouponHistory = this.shopCouponHistoryMapper.selectOne(qw);
            shopCouponHistory.setUseStatus(StateEnums.COUPON_USED.getCode());
            shopCouponHistory.setUseTime(DateUtils.newDateTime());
            shopCouponHistory.setOrderId(order.getId());
            this.shopCouponHistoryMapper.updateById(shopCouponHistory);
        }
        // 6. ??????????????????ID??????????????????????????????
        Long cartId = productDtoList.get(0).getCartId();
        if (cartId != null) {
            // ???????????????????????????????????????????????????
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
        // ??????
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
        // ??????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = shopOrderPayMapper.selectOne(qw);
        log.info("??????????????????????????????????????????{}??????????????????{}", trade_no, JSON.toJSONString(shopOrderPay));
        // ??????????????????
        ShopOrder order = this.shopOrderMapper.selectById(orderId);
        log.info("????????????????????????????????????{}??????????????????{}", orderId, JSON.toJSONString(order));
        // 1. ??????????????????
        order.setStatus(OrderStateEnum.WAIT_SEND.getCode());
        order.setPaymentTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(order);
        // 2. ????????????????????????
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.SYSTEM.getType());
        orderHistory.setOrderStatus(OrderStateEnum.WAIT_SEND.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
        // 3. ??????????????????
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
        // 4. ????????????????????????
        QueryWrapper qw2=new QueryWrapper();
        qw2.eq(ShopUserStatistic.COL_USER_ID,user.getId());
        ShopUserStatistic shopUserStatistic = this.shopUserStatisticMapper.selectOne(qw2);
        shopUserStatistic.setOrderCount(shopUserStatistic.getOrderCount() + 1);
        shopUserStatistic.setConsumeAmount(shopUserStatistic.getConsumeAmount().add(order.getPayAmount()));
        if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
            shopUserStatistic.setCouponCount(shopUserStatistic.getCouponCount() - 1);
        }
        this.shopUserStatisticMapper.updateById(shopUserStatistic);
        // 5. ????????????????????????
        shopOrderPay.setOrderNo(trade_no);
        shopOrderPay.setStatus(OrderStateEnum.PAY_SUCCESS.getCode());
        this.shopOrderPayMapper.updateById(shopOrderPay);
        // ??????openid??????????????????
        order.setOpenId(user.getOpenId());
        return order;
    }

    @Override
    public Page<ShopOrderVo> getMyOrder(Page<ShopOrderVo> page,LoginUser loginUser) {
        // ?????????????????????????????????????????????
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
        // ????????????ID?????????????????????????????????????????????
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
        // ??????id????????????
        ShopOrder order = this.shopOrderMapper.selectById(id);
        ShopOrderVo vo = new ShopOrderVo();
        BeanUtils.copyProperties(order, vo);
        // ????????????id???????????????
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
        // ??????????????????
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
        // ??????????????????
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
        // ??????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = shopOrderPayMapper.selectOne(qw);
        // ????????????????????????
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
        // ????????????
        ShopOrder shopOrder=new ShopOrder();
        shopOrder.setId(orderId);
        shopOrder.setDeleted(StateEnums.DELETED.getCode());
        this.shopOrderMapper.updateById(shopOrder);
        // ??????????????????
        ShopOrderItem shopOrderItem=new ShopOrderItem();
        shopOrderItem.setDeleted(StateEnums.DELETED.getCode());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,orderId);
        this.shopOrderItemMapper.update(shopOrderItem,qw);
    }

    @Override
    public ShopOrderPay cancelOrderById(Long orderId,LoginUser loginUser) {
        // ????????????
        ShopOrder shopOrder1 = this.shopOrderMapper.selectById(orderId);
        // ??????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,orderId);
        List<ShopOrderItem> shopOrderItems = this.shopOrderItemMapper.selectList(qw);
        // ????????????????????????
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopOrderPay.COL_ORDER_ID,orderId);
        ShopOrderPay shopOrderPay = this.shopOrderPayMapper.selectOne(qw1);
        // ??????????????????
        ShopOrder shopOrder=new ShopOrder();
        shopOrder.setId(orderId);
        shopOrder.setStatus(OrderStateEnum.INVALID.getCode());
        shopOrder.setUpdateTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(shopOrder);
        // ??????????????????
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(orderId);
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.INVALID.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
        // ??????,????????????????????????????????????
        if(shopOrder1.getStatus()!=OrderStateEnum.WAIT_PAY.getCode()){
            //??????????????????
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
                shopOrderReturnApply.setReason("???????????????");
                shopOrderReturnApply.setHandleMan(OperateEnum.SYSTEM.getType());
                shopOrderReturnApply.setReceiveMan(shopOrder1.getReceiverName());
                this.shopOrderReturnApplyMapper.insert(shopOrderReturnApply);
            }
            return shopOrderPay;
        }
        return null;
    }

    @Override
    @Transactional
    public ShopOrder createSecKillOrder(ShopSeckill shopSeckill, Long userId) {
        // ????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopUserAddress.COL_USER_ID,userId);
        qw.eq(ShopUserAddress.COL_DEFAULT_STATUS,StateEnums.ADDRESS_DEFAULT.getCode());
        ShopUserAddress address = this.shopUserAddressMapper.selectOne(qw);
        // 4. ????????????????????????????????????????????????
        ShopOrder order = new ShopOrder();
        order.setId(idWorker.nextId());
        order.setCreateBy(userId.toString());
        order.setTotalAmount(shopSeckill.getProductOldPrice());
        order.setPayAmount(shopSeckill.getProductPrice());
        order.setCouponAmount(BigDecimal.ZERO);
        order.setDeliveryCompany("????????????");
        order.setDeliverySn(idWorker.nextId() + "");
        order.setReceiverPostCode("061200");
        if(address!=null){
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhoneNumber());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverCity(address.getCity());
            order.setReceiverRegion(address.getRegion());
            order.setReceiverDetailAddress(address.getDetailAddress());
        }else{
            order.setReceiverName("?????????????????????");
            order.setReceiverPhone("?????????????????????");
            order.setReceiverProvince("?????????????????????/?????????");
            order.setReceiverCity("?????????????????????");
            order.setReceiverRegion("??????????????????");
            order.setReceiverDetailAddress("??????????????????");
        }
        order.setCreateTime(DateUtils.newDateTime());
        order.setOrderType(OrderStateEnum.OrderType_SecKill.getCode());
        order.setFreightAmount(BigDecimal.ZERO);

        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.WAIT_PAY.getCode());
        // ???????????????
        ShopOrderItem item = new ShopOrderItem();
        item.setId(idWorker.nextId());
        item.setCreateTime(DateUtils.newDateTime());
        item.setOrderId(order.getId());
        item.setProductId(shopSeckill.getId());
        item.setProductPic(shopSeckill.getProductPic());
        item.setProductName(shopSeckill.getProductName());
        item.setProductBrand(shopSeckill.getBrandName());
        item.setProductPrice(shopSeckill.getProductPrice());
        item.setProductQuantity(1);
        item.setProductCategoryId(shopSeckill.getCategoryId());
        item.setIntegration(0);
        // ????????????????????????????????????????????????????????????
        this.shopOrderMapper.insert(order);
        this.shopOrderHistoryMapper.insert(orderHistory);
        this.shopOrderItemMapper.insert(item);
        this.shopProductMapper.updateBySecKillStock(shopSeckill.getProductId());
        return order;
    }

    @Override
    public ShopOrderItem selectItem(Long id) {
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopOrderItem.COL_ORDER_ID,id);
        List<ShopOrderItem> list = this.shopOrderItemMapper.selectList(qw);
        return list.get(0);
    }

    /**
     * ????????????
     *
     * @param orderDto       ?????????????????????
     * @param productDtoList ??????id?????????
     * @param productList    ????????????
     * @param address        ??????
     * @param totalMoney     ?????????
     * @param totalPoint     ?????????
     * @param payAmount      ????????????
     * @param couponAmount   ?????????????????????
     * @param loginUser      ?????????
     * @return
     */
    private ShopOrder saveOrder(OrderDto orderDto, List<OrderProductDto> productDtoList, List<ShopProduct> productList, ShopUserAddress address, BigDecimal totalMoney, BigDecimal totalPoint, BigDecimal payAmount, BigDecimal couponAmount, LoginUser loginUser) {
        // 4. ????????????????????????????????????????????????
        ShopOrder order = new ShopOrder();
        order.setId(idWorker.nextId());
        order.setCreateBy(loginUser.getUsername());
        order.setTotalAmount(totalMoney);
        order.setPayAmount(payAmount);
        order.setCouponAmount(couponAmount);
        order.setDeliveryCompany("????????????");
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
        order.setCreateTime(DateUtils.newDateTime());
        // order.setCouponAmount(BigDecimal.ZERO);

        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.WAIT_PAY.getCode());
        // ???????????????
        BigDecimal money=BigDecimal.ZERO;
        List<ShopOrderItem> shopOrderItemList = productList.stream().map(p -> {
            money.add(p.getTransFee());
            ShopOrderItem item = new ShopOrderItem();
            item.setId(idWorker.nextId());
            item.setCreateTime(DateUtils.newDateTime());
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
        // ??????????????????????????????????????????
        order.setFreightAmount(money);
        order.setPayAmount(money.add(payAmount));
        // ????????????????????????????????????????????????????????????
        this.shopOrderMapper.insert(order);
        this.shopOrderHistoryMapper.insert(orderHistory);
        for (ShopOrderItem shopOrderItem : shopOrderItemList) {
            this.shopOrderItemMapper.insert(shopOrderItem);
        }
        return order;
    }
}
