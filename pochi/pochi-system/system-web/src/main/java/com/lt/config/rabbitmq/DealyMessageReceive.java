package com.lt.config.rabbitmq;

import com.lt.dto.OrderProductDto;
import com.lt.enums.OrderStateEnum;
import com.lt.pojo.ShopOrder;
import com.lt.pojo.ShopOrderItem;
import com.lt.service.ShopOrderService;
import com.lt.service.ShopProductService;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Program: rabbitmq-code
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2020-10-15 21:40
 **/
@Component
public class DealyMessageReceive {

    @Reference
    private ShopOrderService shopOrderService;
    @Reference
    private ShopProductService shopProductService;
    @Reference
    private ShopSeckillService shopSeckillService;

    @RabbitListener(queuesToDeclare = @Queue("pay.queue"))
    public void onMessage(String orderId, Message message, Channel channel) {
        try {
            System.out.println("欢迎来到新世界=================");
            // 消息的投递Id
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
            System.out.println("消息接受成功：" + orderId);
            // 时间超时，设置订单的状态是无效订单
            ShopOrder byId = this.shopOrderService.getById(Long.valueOf(orderId));
            if (byId.getStatus() == OrderStateEnum.WAIT_PAY.getCode()) {
                ShopOrder shopOrder = new ShopOrder();
                shopOrder.setId(Long.valueOf(orderId));
                shopOrder.setStatus(OrderStateEnum.INVALID.getCode());
                this.shopOrderService.changeOrderStatus(shopOrder);
                // 还原库存
                ShopOrderItem shopOrderItem = shopOrderService.selectItem(Long.valueOf(orderId));
                OrderProductDto orderProductDto = new OrderProductDto();
                orderProductDto.setCount(0 - shopOrderItem.getProductQuantity());
                orderProductDto.setProductId(shopOrderItem.getProductId());
                shopProductService.updateStock(orderProductDto);
                // 如果是秒杀订单还原秒杀库存
//                if(byId.getOrderType()==OrderStateEnum.OrderType_SecKill.getCode()){
//                    shopSeckillService.updateStockAdd(shopOrderItem.getProductId());
//                }
            }
            System.out.println("签收成功的时间是：" + DateUtils.newDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
