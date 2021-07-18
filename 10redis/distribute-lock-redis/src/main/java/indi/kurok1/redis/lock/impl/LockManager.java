package indi.kurok1.redis.lock.impl;

import indi.kurok1.redis.lock.DistributeLock;

/**
 * 分布式锁管理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class LockManager {



    public static DistributeLock getReentrantDistributeLock(String lockKey) {
        return new ReentrantDistributeLock(lockKey);
    }

}
