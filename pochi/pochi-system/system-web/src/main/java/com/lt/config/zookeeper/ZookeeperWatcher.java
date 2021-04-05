package com.lt.config.zookeeper;

import com.lt.constant.CoreConstant;
import com.lt.controller.marketing.ShopSecKillController;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperWatcher implements Watcher, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperWatcher.class);

    private static ApplicationContext applicationContext;

    private ZooKeeper zooKeeper;

    /**
     * 监听到节点发生变化，listen线程会调用process方法
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {  //zk连接成功通知事件
            logger.info("========================zookeeper连接成功========================");
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                if (zooKeeper==null){
                    zooKeeper = (ZooKeeper)applicationContext.getBean(ZooKeeper.class);
                }

                try{
                    if (zooKeeper.exists(CoreConstant.ZK_PRODUCT_SOLD_OUT_FLAG,false) == null){
                        zooKeeper.create(CoreConstant.ZK_PRODUCT_SOLD_OUT_FLAG,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {  //zk目录节点数据变化通知事件
                try {
                    String path = watchedEvent.getPath() ;
                    String soldOutFlag = new String(zooKeeper.getData(path,true,new Stat()));
                    logger.info("zookeeper数据节点修改变动, path={}, value=(}", path, soldOutFlag);
                    if ("false".equals(soldOutFlag)) {
                        String productId = path.substring(path.lastIndexOf("/")+1,path.length());
                        ShopSecKillController.getProductSoldOutMap().remove(productId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
