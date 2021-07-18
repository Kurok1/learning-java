package indi.kurok1.redis;

import indi.kurok1.redis.common.connect.ConnectionProvider;
import indi.kurok1.redis.domain.OrderWrapper;
import redis.clients.jedis.Jedis;

/**
 * 订阅者线程
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class SubscriberThread implements Runnable {

    private final ConnectionProvider connectionProvider = ConnectionProvider.getInstance();

    @Override
    public void run() {
        Jedis jedis = null;
        try {
            jedis = connectionProvider.getConnection();
            jedis.subscribe(new OrderSubscriber(), OrderWrapper.CHANNEL);
        } catch (Exception e) {
            System.out.println(String.format("subsrcibe channel error, %s", e));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
