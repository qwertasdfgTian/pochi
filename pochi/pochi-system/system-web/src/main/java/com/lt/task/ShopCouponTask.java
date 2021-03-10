package com.lt.task;

import com.lt.service.ShopCouponService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/22 21:39
 * @Version 1.0
 */
@Component
public class ShopCouponTask {

    @Reference
    private ShopCouponService shopCouponService;

    /**
     * 更新优惠券过期时间
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshCouponStatus() {
        shopCouponService.updateTimeoutCoupon();
    }

}
