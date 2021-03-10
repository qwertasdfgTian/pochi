package com.lt.controller.commodity;

import com.lt.config.pay.AlipayConfig;
import com.lt.config.pay.PayService;
import com.lt.controller.BaseController;
import com.lt.dto.OrderReturnTemplateDto;
import com.lt.dto.OrderTemplateDto;
import com.lt.dto.TemplateValue;
import com.lt.pojo.ShopOrderPay;
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
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private WxService wxService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 提交订单
     *
     * @param orderDto
     * @return
     */
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    //@HystrixCommand
    public Result<?> createOrder(@RequestBody OrderDto orderDto) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        ShopOrder order = this.shopOrderService.createOrder(orderDto,loginUser);
        // 发送延时消息
        System.out.println("当前的时间是："+DateUtils.newDateTime());
        rabbitTemplate.convertAndSend("dealy",order.getId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("1800000");
                return message;
            }
        });
        System.out.println("消息发送成功");
        return new Result<>("订单提交成功", order);
    }

    /**
     * 创建支付订单
     *
     * @param order
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createPayOrder", method = RequestMethod.POST)
//    @HystrixCommand
    public Result<?> createPayOrder(@RequestBody ShopOrder order) throws Exception {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        // 根据订单id查询订单的详细信息
        order = this.shopOrderService.getById(order.getId());
        String outTradeNo = idWorker.nextId() + "";
        this.shopOrderService.createPayOrder(order,loginUser,outTradeNo);
        //2,因为是支付宝支付，所以我们要返回给页面一个二维码
        String subject="喵喵宠物商城支付平台";
        String totalAmount=order.getPayAmount().multiply(new BigDecimal(1)) + "";
        String undiscountableAmount=null;
        String body="宠物商品";
        String notifyUrl= AlipayConfig.notifyUrl+order.getId();
        Map<String, Object> pay = PayService.pay(outTradeNo, subject, totalAmount, undiscountableAmount, body, notifyUrl);
        String qrCode = pay.get("qrCode").toString();
        if(StringUtils.isNotBlank(qrCode)){
            //创建支付成功
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
     * 查看订单状态
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/queryOrderPayOrderId/{orderId}", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> queryOrderPayOrderId(@PathVariable String orderId) {
        ShopOrderPay orderPay = this.shopOrderService.queryOrderPayOrderId(orderId);
        return new Result<>("操作成功", orderPay);
    }
    /**
     * 前台分页查询
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
     * 根据ID查询
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
     * 查询订单历史
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
     * 根据id收货
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/receiveById/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> receiveById(@PathVariable Long id) {
        this.shopOrderService.receiveById(id);
        return new Result<>("收货成功");
    }

    /**
     * 后台分页查询
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
     * 改变订单状态
     *
     * @param order
     * @return
     */
    @RequestMapping(value = "/changeOrderStatus", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> changeOrderStatus(@RequestBody ShopOrder order) {
        this.shopOrderService.changeOrderStatus(order);
        return new Result<>("操作成功");
    }

    /**
     * 查询当月订单
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
     * 查询每种订单的占比
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
     * 根据创建人查询各种订单的数量
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
     * 根据id删除订单
     *
     * @return
     */
    @RequestMapping(value = "/deleteOrderById/{orderId}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> deleteOrderById(@PathVariable Long orderId) {
        this.shopOrderService.deleteOrderById(orderId);
        return new Result<>("删除成功");
    }

    /**
     * 根据id取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrderById/{orderId}", method = RequestMethod.DELETE)
    //@HystrixCommand
    public Result<?> cancelOrderById(@PathVariable Long orderId) {
        LoginUser loginUser=ShiroUtils.getLoginUser();
        ShopOrderPay shopOrderPay=this.shopOrderService.cancelOrderById(orderId,loginUser);
        //找到当前退费单之前的收费单的ID,然后开始退费
        //因为是支付宝退费，所以调用支付宝的接口
        if(shopOrderPay!=null){
            String outTradeNo=shopOrderPay.getOrderId()+"";
            String tradeNo=shopOrderPay.getOrderNo();
            String refundAmount=shopOrderPay.getPayAmount().toString();
            String refundReason="不想要了";
            String outRequestNo=idWorker.nextId()+"";
            Map<String, Object> map = PayService.payBack(outTradeNo, tradeNo, refundAmount, refundReason, outRequestNo);
            if(map.get("code").toString().equals("200")){
                ShopOrder order = this.shopOrderService.getById(orderId);
                // 通知用户退费成功
                OrderReturnTemplateDto template = new OrderReturnTemplateDto();
                template.setAddress(new TemplateValue(order.getReceiverProvince() + order.getReceiverCity() + order.getReceiverRegion() + order.getReceiverDetailAddress()));
                template.setAmount(new TemplateValue('￥'+order.getPayAmount().toString()));
                template.setOrderId(new TemplateValue(order.getId() + ""));
                template.setOrderTime(new TemplateValue(DateUtils.newDateTime()));
                template.setReturnFS(new TemplateValue("支付宝"));
                this.wxService.pushMessage(template, "DRRjCAJnwYdqTD84dFDYIPephHQr9NrIDj3ttyWHIwU", loginUser.getOpenId());
                return new Result<>("退费成功");
            }else{
                return new Result<>(map.get("msg").toString());
            }
        }
        return new Result<>("取消成功");
    }

    /**
     * 计算订单剩余时间
     *
     * @return
     */
    @RequestMapping(value = "/orderRemainingTime/{createTime}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> orderRemainingTime(@PathVariable String createTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        Date now = df.parse(DateUtils.newDateTime());
        // 创建时间
        Date date=df.parse(createTime);
        long l=now.getTime()-date.getTime();
        System.out.println("一共"+l+"毫秒");
        Long time=1800000L-l;
        if(time>0){
            return new Result<>(time);
        }else{
            return new Result<>("订单已超时");
        }
    }

}
