package indi.kurok1.redis.lock.impl;

import indi.kurok1.redis.lock.AbstractDistributeLock;
import indi.kurok1.redis.common.config.ClusterConfig;
import indi.kurok1.redis.common.connect.ConnectionProvider;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 可重入的分布式锁
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class ReentrantDistributeLock extends AbstractDistributeLock {


    private final String lockKey;

    private Thread thread;

    private final ClusterConfig config;

    private final Jedis jedis = ConnectionProvider.getInstance().getConnection();

    public ReentrantDistributeLock(String lockKey) {
        this.lockKey = lockKey;
        config = ClusterConfig.instance();
    }

    public ReentrantDistributeLock(String lockKey, ClusterConfig config) {
        this.lockKey = lockKey;
        this.config = config;
    }

    /**
     * recordKey用于记录当前线程在这个锁上的重入次数
     * 同时也是lockKey对应的value
     * @return
     */
    public String getRecordKey() {
        return generateThreadKey(config.getClusterName(), this.thread);
    }


    @Override
    public boolean isHeld(String clusterName, Thread thread) {
        if (!jedis.exists(getLockKey()))
            return false;
        String record = jedis.get(getLockKey());
        String validate = generateThreadKey(config.getClusterName(), thread);
        return record.equals(validate);
    }

    @Override
    public String getLockKey() {
        return this.lockKey;
    }

    @Override
    public void lock(long autoReleaseTime, TimeUnit timeUnit) {
        while (!canLock()){
        }
        lockInternal();
        if (autoReleaseTime > 0) {
            jedis.expire(getLockKey(), new Long(timeUnit.toSeconds(autoReleaseTime)).intValue());
            jedis.expire(getRecordKey(), new Long(timeUnit.toSeconds(autoReleaseTime)).intValue());
        }
    }

    private void lockInternal() {
        this.thread = Thread.currentThread();
        jedis.set(getLockKey(), getRecordKey());
        if (jedis.exists(getRecordKey())) {
            //重入次数++
            String num = jedis.get(getRecordKey());
            Integer times = Integer.decode(num);
            times++;
            jedis.set(getRecordKey(), times.toString());
        } else {
            jedis.set(getRecordKey(), "1");//重入次数为1
        }
    }

    @Override
    public void lockInterruptibly(long autoReleaseTime, TimeUnit timeUnit) throws InterruptedException {
        String clusterName = config.getClusterName();
        while (!isHeldByCurrentThread(clusterName)) {
            //检查状态
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
        }
        lockInternal();
        if (autoReleaseTime > 0) {
            jedis.expire(getLockKey(), new Long(timeUnit.toSeconds(autoReleaseTime)).intValue());
            jedis.expire(getRecordKey(), new Long(timeUnit.toSeconds(autoReleaseTime)).intValue());
        }
    }

    @Override
    public boolean tryLock(long maxWaitTime, long autoReleaseTime, TimeUnit unit) throws InterruptedException {
        long waitingMillis = unit.toMillis(maxWaitTime);
        long begin = System.currentTimeMillis();
        while (!canLock()) {
            long current = System.currentTimeMillis();
            if (current - begin > waitingMillis)
                return false;
        }
        if (canLock()) {
            lock(autoReleaseTime, unit);
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock() {
        boolean flag = canLock();
        if (flag) {
            lock();
        }
        return flag;
    }

    private boolean canLock() {
        if (!jedis.exists(getLockKey()))
            return true;
        String recordKey = generateThreadKey(config.getClusterName(), Thread.currentThread());
        String cached = jedis.get(getLockKey());
        if (cached.equals(recordKey)) {
            return true;
        } else return false;
    }

    @Override
    public  void unlock() {
        if (!isHeldByCurrentThread(config.getClusterName()))
            return;
        String num = jedis.get(getRecordKey());
        Integer times = Integer.decode(num);
        times--;
        if (times <= 0) {
            jedis.del(getLockKey(), getRecordKey());
        } else {
            jedis.set(getRecordKey(), times.toString());
        }
    }


    @Override
    public Condition newCondition() {
        //todo 待实现
        throw new UnsupportedOperationException();
    }
}
