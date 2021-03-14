package com.lt.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Program: rabbitmq-code
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2020-10-15 20:53
 **/
@Configuration
public class DealyMessageConfig {

    //创建延时队列
    @Bean
    public Queue dealyQueue(){
        HashMap<String,Object> args=new HashMap<>();
        // 把一个队列修改为延迟队列
        args.put("x-message-ttl",1800000) ; // 消息的最大存活时间
        args.put("x-dead-letter-exchange","DeadLetter.exc") ; // 该队列里面的消息死了，去那个交换机
        args.put("x-dead-letter-routing-key","DeadLetter.key") ; // 该队列里面的消息死了，去那个交换机, 由那个路由 key 路由他
        Queue queue=new Queue("dealy",true,false,false,args);
        return queue;
    }

    //创建死信交换机
    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange("DeadLetter.exc");
    }

    //创建新的队列
    @Bean
    public Queue newQueue(){
        return new Queue("pay.queue");
    }

    //绑定死信交互机
    @Bean
    public Binding newAndDeadLetterExchange(){
        return BindingBuilder.bind(newQueue()).to(deadLetterExchange()).with("DeadLetter.key");
    }
}
