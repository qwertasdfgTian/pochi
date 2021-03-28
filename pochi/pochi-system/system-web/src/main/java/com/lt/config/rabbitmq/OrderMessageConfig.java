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
public class OrderMessageConfig {

    //声明交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("direct");
    }

    //创建新的队列1
    @Bean
    public Queue orderqueue(){
        return new Queue("orderqueue");
    }


    //绑定队列交互机
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(orderqueue()).to(directExchange()).with("order");
    }
}
