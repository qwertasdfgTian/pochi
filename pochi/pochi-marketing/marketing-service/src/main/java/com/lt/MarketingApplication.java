package com.lt;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.lt.mapper"})
@EnableDubbo
public class MarketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketingApplication.class,args);
        System.out.println("营销子模块启动成功");
    }
}
