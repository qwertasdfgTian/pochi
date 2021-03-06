package com.lt.controller.commodity;

import com.lt.config.pay.AlipayConfig;
import com.lt.config.pay.PayService;
import com.lt.controller.BaseController;
import com.lt.dto.OrderReturnTemplateDto;
import com.lt.dto.OrderTemplateDto;
import com.lt.dto.TemplateValue;
import com.lt.enums.OrderStateEnum;
import com.lt.enums.StateEnums;
import com.lt.pojo.ShopOrderItem;
import com.lt.pojo.ShopOrderPay;
import com.lt.service.ShopSeckillService;
import com.lt.service.WxService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import com.lt.pojo.ShopOrder;
import com.lt.pojo.ShopOrderHistory;
import com.lt.dto.OrderDto;
import com.lt.service.ShopOrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/20 20:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/order")
public class ShopOrderController extends BaseController {

    @Reference
    private ShopOrderService shopOrderService;
    @Reference
    private ShopSeckillService shopSeckillService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private WxService wxService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * ????????????
     *
     * @param orderDto
     * @return
     */
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    //@HystrixCommand
    public Result<?> createOrder(@RequestBody OrderDto orderDto) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        ShopOrder order = this.shopOrderService.createOrder(orderDto,loginUser);
        // ??????????????????
        System.out.println("?????????????????????"+DateUtils.newDateTime());
        rabbitTemplate.convertAndSend("dealy",order.getId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("1800000");
                return message;
            }
        });
        System.out.println("??????????????????");
        return new Result<>("??????????????????", order);
    }

    /**
     * ??????????????????
     *
     * @param order
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createPayOrder", method = RequestMethod.POST)
//    @HystrixCommand
    public Result<?> createPayOrder(@RequestBody ShopOrder order) throws Exception {
        ShopOrderVo shopOrderVo = this.shopOrderService.get(order.getId());
        if (shopOrderVo.getStatus() == OrderStateEnum.INVALID.getCode()){
            return new Result<>("???????????????");
        }
        LoginUser loginUser = ShiroUtils.getLoginUser();
        // ????????????id???????????????????????????
        order = this.shopOrderService.getById(order.getId());
        String outTradeNo = idWorker.nextId() + "";
        this.shopOrderService.createPayOrder(order,loginUser,outTradeNo);
        //2,????????????????????????????????????????????????????????????????????????
        String subject="??????????????????????????????";
        String totalAmount=order.getPayAmount().multiply(new BigDecimal(1)) + "";
        String undiscountableAmount=null;
        String body="????????????";
        String notifyUrl= AlipayConfig.notifyUrl+order.getId();
        Map<String, Object> pay = PayService.pay(outTradeNo, subject, totalAmount, undiscountableAmount, body, notifyUrl);
        String qrCode = pay.get("qrCode").toString();
        if(StringUtils.isNotBlank(qrCode)){
            //??????????????????
            Map<String,Object> map=new HashMap<>();
            map.put("orderId",order.getId());
            map.put("allAmount",totalAmount);
            map.put("payUrl",qrCode);
            return new Result<>(map);
        }else{
            return new Result<>(pay.get("msg").toString());
        }
    }

    /**
     * ??????????????????
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/queryOrderPayOrderId/{orderId}", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> queryOrderPayOrderId(@PathVariable String orderId) {
        ShopOrderPay orderPay = this.shopOrderService.queryOrderPayOrderId(orderId);
        return new Result<>("????????????", orderPay);
    }
    /**
     * ??????????????????
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getMyOrder", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getMyOrder(@RequestBody Page<ShopOrderVo> page) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        page = this.shopOrderService.getMyOrder(page,loginUser);
        return new Result<>(page);
    }

    /**
     * ??????ID??????
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> get(@PathVariable Long id) {
        ShopOrderVo vo = this.shopOrderService.get(id);
        return new Result<>(vo);
    }

    /**
     * ??????????????????
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getHistory/{id}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getHistory(@PathVariable Long id) {
        List<ShopOrderHistory> list = this.shopOrderService.getHistory(id);
        return new Result<>(list);
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/receiveById/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> receiveById(@PathVariable Long id) {
        this.shopOrderService.receiveById(id);
        return new Result<>("????????????");
    }

    /**
     * ??????????????????
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopOrder> page) {
        page = this.shopOrderService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * ??????????????????
     *
     * @param order
     * @return
     */
    @RequestMapping(value = "/changeOrderStatus", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> changeOrderStatus(@RequestBody ShopOrder order) {
        this.shopOrderService.changeOrderStatus(order);
        return new Result<>("????????????");
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/monthOrder", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> monthOrder() {
        List<OrderMonthVo> list = this.shopOrderService.monthOrder();
        return new Result<>(list);
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/orderPoint", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> orderPoint() {
        List<OrderPointVo> list = this.shopOrderService.orderPoint();
        return new Result<>(list);
    }

    /**
     * ??????????????????????????????????????????
     * @return
     */
    @RequestMapping(value = "/getOrderTypeNum", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getOrderTypeNum() {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        OrderTypeNumVo orderTypeNum = this.shopOrderService.getOrderTypeNum(loginUser);
        return new Result<>(orderTypeNum);
    }

    /**
     * ??????id????????????
     *
     * @return
     */
    @RequestMapping(value = "/deleteOrderById/{orderId}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> deleteOrderById(@PathVariable Long orderId) {
        this.shopOrderService.deleteOrderById(orderId);
        return new Result<>("????????????");
    }

    /**
     * ??????id????????????
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrderById/{orderId}", method = RequestMethod.DELETE)
    //@HystrixCommand
    public Result<?> cancelOrderById(@PathVariable Long orderId) {
        LoginUser loginUser=ShiroUtils.getLoginUser();
        ShopOrderPay shopOrderPay=this.shopOrderService.cancelOrderById(orderId,loginUser);
        //??????????????????????????????????????????ID,??????????????????
        //?????????????????????????????????????????????????????????
        if(shopOrderPay!=null){
            String outTradeNo=shopOrderPay.getOrderId()+"";
            String tradeNo=shopOrderPay.getOrderNo();
            String refundAmount=shopOrderPay.getPayAmount().toString();
            String refundReason="????????????";
            String outRequestNo=idWorker.nextId()+"";
            Map<String, Object> map = PayService.payBack(outTradeNo, tradeNo, refundAmount, refundReason, outRequestNo);
            if(map.get("code").toString().equals("200")){
                ShopOrder order = this.shopOrderService.getById(orderId);
                // ????????????????????????
                OrderReturnTemplateDto template = new OrderReturnTemplateDto();
                template.setAddress(new TemplateValue(order.getReceiverProvince() + order.getReceiverCity() + order.getReceiverRegion() + order.getReceiverDetailAddress()));
                template.setAmount(new TemplateValue('???'+order.getPayAmount().toString()));
                template.setOrderId(new TemplateValue(order.getId() + ""));
                template.setOrderTime(new TemplateValue(DateUtils.newDateTime()));
                template.setReturnFS(new TemplateValue("?????????"));
                this.wxService.pushMessage(template, "DRRjCAJnwYdqTD84dFDYIPephHQr9NrIDj3ttyWHIwU", loginUser.getOpenId());
                return new Result<>("????????????");
            }else{
                return new Result<>(map.get("msg").toString());
            }
        }
        return new Result<>("????????????");
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/orderRemainingTime/{orderId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> orderRemainingTime(@PathVariable Long orderId) throws ParseException {
        ShopOrderVo shopOrderVo = this.shopOrderService.get(orderId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // ??????????????????
        Date now = df.parse(DateUtils.newDateTime());
        // ????????????
        Date date=df.parse(shopOrderVo.getCreateTime());
        long l=now.getTime()-date.getTime();
        System.out.println("??????"+l+"??????");
        // ??????????????????
        Long time=null;
        if(shopOrderVo.getOrderType()== OrderStateEnum.OrderType_ORDINARY.getCode()){
            time=1800000L-l;
        }
        if(shopOrderVo.getOrderType()==OrderStateEnum.OrderType_SecKill.getCode()){
            ShopOrderItem shopOrderItem=shopOrderService.selectItem(shopOrderVo.getId());
            String canceltime = this.shopSeckillService.selectCancelTime(shopOrderItem.getProductId());
            time=Long.parseLong(canceltime)*60*1000-l;
        }
        if(time>0){
            return new Result<>(time);
        }else{
            return new Result<>("???????????????");
        }
    }

}
