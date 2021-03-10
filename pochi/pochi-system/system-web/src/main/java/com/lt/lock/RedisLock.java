package com.lt.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/21 22:21
 * @Version 1.0
 */
public interface RedisLock {

    /**
     * 上锁，默认超时时间5秒
     *
     * @param key
     * @return
     */
    boolean lock(String key);

    /**
     * 解锁
     *
     * @param key
     */
    void unlock(String key);

    /**
     * 上锁，指定超时时间，单位毫秒
     *
     * @param key
     * @param timeout
     * @return
     */
    boolean lock(String key, long timeout);

    /**
     * 上锁，指定超时时间和单位
     *
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    boolean lock(String key, long timeout, TimeUnit timeUnit);

    /**
     * 尝试获取锁
     * @param lockKey 锁key
     * @param watTime 等待时间，在等待之间之内会一直尝试获取锁
     * @param leaseTime 锁失效时间
     * @return
     */
    boolean tryLock(String lockKey, long watTime, long leaseTime);
}
