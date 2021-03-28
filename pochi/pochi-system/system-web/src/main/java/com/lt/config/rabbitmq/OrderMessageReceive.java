package com.lt.config.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.lt.dto.GoShopSeckillDto;
import com.lt.dto.OrderProductDto;
import com.lt.enums.OrderStateEnum;
import com.lt.pojo.ShopOrder;
import com.lt.pojo.ShopOrderItem;
import com.lt.pojo.ShopSeckill;
import com.lt.service.ShopOrderService;
import com.lt.service.ShopProductService;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

/**
 * @Program: rabbitmq-code
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2020-10-15 21:40
 **/
@Component
public class OrderMessageReceive {

    @Reference
    private ShopOrderService shopOrderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Reference
    private ShopSeckillService shopSeckillService;

    @RabbitListener(queuesToDeclare = @Queue("orderqueue"))
    public void onMessage(String goShopSeckillDto, Message message, Channel channel){
        try {
            System.out.println("欢迎来到新世界=================");
            // 消息的投递Id
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag,false);
            System.out.println("消息接受成功："+goShopSeckillDto);
            // 拿到消息内容创建订单
            GoShopSeckillDto shopSeckill = JSON.parseObject(goShopSeckillDto, GoShopSeckillDto.class);
            List<ShopSeckill> list = this.shopSeckillService.getByProductId(shopSeckill.getProductId());

            ShopOrder order = this.shopOrderService.createSecKillOrder(list.get(0), shopSeckill.getUserId());
            // 发送延时消息
            System.out.println("当前的时间是："+DateUtils.newDateTime());
            this.rabbitTemplate.convertAndSend("dealy",order.getId(), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setExpiration(Long.parseLong(list.get(0).getCancelTime())*60*1000+"");
                    return message;
                }
            });
            System.out.println("消息发送成功");

            System.out.println("签收成功的时间是："+ DateUtils.newDateTime());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
