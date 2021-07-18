package indi.kurok1.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.kurok1.redis.common.connect.ConnectionProvider;
import indi.kurok1.redis.domain.OrderWrapper;
import redis.clients.jedis.Jedis;

/**
 * 订单发布
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class OrderPublisher {

    private final ConnectionProvider connectionProvider = ConnectionProvider.getInstance();

    public void publishOrder(OrderWrapper order) {
        Jedis jedis = this.connectionProvider.getConnection();
        try {
            jedis.publish(OrderWrapper.CHANNEL, new ObjectMapper().writeValueAsString(order));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }



}
