package indi.kurok1.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁抽线实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public abstract class AbstractDistributeLock implements DistributeLock {


    @Override
    public void lock() {
        lock(-1, TimeUnit.SECONDS);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock(-1, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock(time, -1, unit);
    }

    public String generateThreadKey(String clusterName, Thread thread) {
        return String.format("%s-%s-%s", getLockKey(), clusterName, thread.getId());
    }

    public boolean isHeldByCurrentThread(String clusterName) {
        return isHeld(clusterName, Thread.currentThread());
    }

    /**
     * 判断当前锁是否被指定线程持有
     * @param clusterName 客户端实例名称
     * @param thread 线程
     * @return 是否被指定线程持有
     */
    public abstract boolean isHeld(String clusterName, Thread thread);

}
