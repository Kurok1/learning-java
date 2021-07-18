package indi.kurok1.redis.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁api定义
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 * @ses Lock
 */
public interface DistributeLock extends Lock {

    /**
     * @return lock key
     */
    String getLockKey();

    /**
     * 让当前线程持有锁
     * @param autoReleaseTime 自动释放时间，如果小于等于0, 则代表不会自动释放
     * @param timeUnit
     */
    void lock(long autoReleaseTime, TimeUnit timeUnit);

    /**
     * 让当前线程持有锁，除非被其他线程打断
     * @param autoReleaseTime 自动释放时间，如果小于等于0, 则代表不会自动释放
     * @param timeUnit 时间单位
     * @throws InterruptedException 获取锁过程中被其他线程打断
     */
    void lockInterruptibly(long autoReleaseTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 让当前线程持有锁，这个过程不会被其他线程打断
     * @param maxWaitTime 持有锁最大等待时间
     * @param autoReleaseTime 获取锁后自动释放时间，如果小于等于0, 则代表不会自动释放
     * @param unit 时间单位
     * @return 是否获取成功
     * @throws InterruptedException
     */
    boolean tryLock(long maxWaitTime, long autoReleaseTime, TimeUnit unit) throws InterruptedException;


}
