package com.lt.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 22:24
 * @Version 1.0
 */
@Component
public class RedisLockImpl implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final long DEFAULT_TIMEOUT = 5000L;

    @Override
    public boolean lock(String key) {
        return lock(key, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unlock(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean lock(String key, long timeout) {
        return lock(key, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean lock(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.boundValueOps(key).setIfAbsent(key, timeout, timeUnit);
    }

    @Override
    public boolean tryLock(String lockKey, long watTime, long leaseTime) {
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < watTime) {
            boolean flag = lock(lockKey, leaseTime, TimeUnit.MILLISECONDS);
            if(flag) {
                return flag;
            }
        }
        return false;
    }
}
