package com.lt;

import com.lt.enums.StateEnums;
import com.lt.mapper.ShopUserMapper;
import com.lt.pojo.ShopUser;
import com.lt.utils.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class RabbitmqSpringbootDealyApplicationTests {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void test() throws IOException {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("当前的时间是："+simpleDateFormat.format(new Date()));
		rabbitTemplate.convertAndSend("dealy", (Object) "我是延时消息", new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				message.getMessageProperties().setExpiration("5000"); //我们延时队列里面 设置的为 10 秒，但这个消息它只想活 5 秒
				return message;
			}
		});
		System.out.println("消息发送成功");
		System.in.read();
	}
}
