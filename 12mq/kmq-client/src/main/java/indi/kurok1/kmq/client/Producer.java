package indi.kurok1.kmq.client;

import indi.kurok1.kmq.client.autoconfig.KmqClientProperties;
import indi.kurok1.kmq.core.MessageHeader;
import indi.kurok1.kmq.core.MessageHeaders;
import indi.kurok1.kmq.core.TextMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public class Producer {

    private final RestTemplate restTemplate;
    private final KmqClientProperties properties;

    private final String POST_PATH = "/api/producer/post";
    private final String RETRY_PATH = "/api/producer/retry";

    public Producer(RestTemplate restTemplate, KmqClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    protected MessageHeaders buildBaseHeaders(String queue) {
        return new MessageHeaders.Builder()
                .set(MessageHeader.CREATED_TIME, String.valueOf(System.currentTimeMillis()/1000))
                .set(MessageHeader.QUEUE_NAME, queue)
                .set(MessageHeader.VERSION, "1.0.0").build();
    }

    public void sendTextMessage(String queue, String text, Consumer<ResponseEntity> consumer) {
        Assert.hasLength(queue, "queue not be empty");

        TextMessage message = new TextMessage(text);
        message.setQueue(queue);
        message.setHeaders(buildBaseHeaders(queue));

        String url = String.format("http://%s:%d%s", this.properties.getServerAddress(), this.properties.getPort(), this.POST_PATH);
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        RequestEntity<TextMessage> entity = new RequestEntity<>(message, HttpMethod.POST, uri);
        ResponseEntity responseEntity = this.restTemplate.postForEntity(url, entity, String.class);
        if (consumer != null)
            consumer.accept(responseEntity);

    }

    //todo 更多类型的消息发送实现

}
