package com.lt.config.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.lt.constant.CoreConstant;
import com.lt.controller.marketing.ShopSecKillController;
import com.lt.dto.GoShopSeckillDto;
import com.lt.dto.OrderProductDto;
import com.lt.enums.OrderStateEnum;
import com.lt.exception.PochiException;
import com.lt.pojo.ShopOrder;
import com.lt.pojo.ShopOrderItem;
import com.lt.pojo.ShopSeckill;
import com.lt.service.ShopOrderService;
import com.lt.service.ShopProductService;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ZooKeeper zooKeeper;

    @RabbitListener(queuesToDeclare = @Queue("orderqueue"))
    public void onMessage(String goShopSeckillDto, Message message, Channel channel) throws KeeperException, InterruptedException {
        try {
            System.out.println("欢迎来到新世界=================");
            // 消息的投递Id
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag,false);
            System.out.println("消息接受成功："+goShopSeckillDto);
            // 拿到消息内容创建订单
            GoShopSeckillDto shopSeckill = JSON.parseObject(goShopSeckillDto, GoShopSeckillDto.class);
            List<ShopSeckill> list = this.shopSeckillService.getByProductId(shopSeckill.getProductId());
            // 扣减秒杀库存
            this.shopSeckillService.updateStock(list.get(0).getId());
            // 开始创建订单
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
            GoShopSeckillDto shopSeckill = JSON.parseObject(goShopSeckillDto, GoShopSeckillDto.class);
            // 消息发送出现异常还原库存
            this.stringRedisTemplate.opsForValue().increment(shopSeckill.getProductId().toString());
            // 移除售完的缓存节点
            if (ShopSecKillController.getProductSoldOutMap().get(shopSeckill.getProductId()) != null) {
                ShopSecKillController.getProductSoldOutMap().remove(shopSeckill.getProductId());
            }
            // 变化zookeeper的值同步所有服务的售完节点
            if (zooKeeper.exists(CoreConstant.getZKSoldOutProductPath(shopSeckill.getProductId()),true) != null){
                zooKeeper.setData(CoreConstant.getZKSoldOutProductPath(shopSeckill.getProductId()),"false".getBytes(),-1);
            }
            throw new PochiException("您的秒杀订单出现异常，抢购失败！");
        }
    }

}
