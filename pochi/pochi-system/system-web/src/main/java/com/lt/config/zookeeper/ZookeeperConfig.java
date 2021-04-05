package com.lt.config.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    @Value("101.200.224.175:2181")
    private String zookeeperAdress;

    @Autowired
    private ZookeeperWatcher zookeeperWatcher;

    @Bean
    public ZooKeeper initZookeeper() throws Exception{
        return new ZooKeeper(zookeeperAdress,60000,zookeeperWatcher);
    }
}
