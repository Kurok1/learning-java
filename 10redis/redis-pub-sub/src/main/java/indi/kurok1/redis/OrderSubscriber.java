package indi.kurok1.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.kurok1.redis.domain.OrderWrapper;
import redis.clients.jedis.JedisPubSub;

/**
 * 订单订阅者
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class OrderSubscriber extends JedisPubSub {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String channel, String message) {
        try {
            OrderWrapper wrapper = objectMapper.readValue(message, OrderWrapper.class);
            System.out.println("received order with id is " + wrapper.getOrderId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println(String.format("subscribe redis channel success, channel %s, subscribedChannels %d",
                channel, subscribedChannels));
    }
}
