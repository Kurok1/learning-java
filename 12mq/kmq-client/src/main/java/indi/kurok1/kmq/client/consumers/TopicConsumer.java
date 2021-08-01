package indi.kurok1.kmq.client.consumers;

import indi.kurok1.kmq.client.Consumer;
import indi.kurok1.kmq.client.autoconfig.KmqClientProperties;
import indi.kurok1.kmq.core.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@Component
public class TopicConsumer extends Consumer {

    public TopicConsumer(RestTemplate restTemplate, KmqClientProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    public void onMessage(Message<?> message) {
        System.out.println("received!");
        System.out.println(message.getPayload());
    }

    @Override
    public String getQueueName() {
        return "topic";
    }
}
