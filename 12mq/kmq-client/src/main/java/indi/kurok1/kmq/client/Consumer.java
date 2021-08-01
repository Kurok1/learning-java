package indi.kurok1.kmq.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.kurok1.kmq.client.autoconfig.KmqClientProperties;
import indi.kurok1.kmq.core.Message;
import indi.kurok1.kmq.core.TextMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费者，需要客户自定义实现消息处理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public abstract class Consumer implements Runnable {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final KmqClientProperties properties;

    private final String FETCH_PATH = "/api/consumer/get";
    private final String RETRY_PATH = "/api/producer/retry";

    public Consumer(RestTemplate restTemplate, KmqClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * 消息处理，保留实现
     * @param message 抓取的消息
     */
    public abstract void onMessage(Message<?> message);

    /**
     * @return 监听队列名称
     */
    public abstract String getQueueName();

    @Override
    public void run() {
        //抓取消息的逻辑，定时执行
        String queueName = getQueueName();
        int fetchSize = this.properties.getDefaultFetchSize();
        if (this.properties.getQueueLimits().containsKey(queueName))
            fetchSize = this.properties.getQueueLimits().get(queueName);
        String url = String.format("http://%s:%d%s/%s/%d", this.properties.getServerAddress(), this.properties.getPort(), this.FETCH_PATH, getQueueName(), fetchSize);
        ResponseEntity<String> entity = this.restTemplate.getForEntity(url,  String.class);
        if (entity != null && entity.getStatusCode().is2xxSuccessful()) {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, TextMessage.class);
            String json = entity.getBody();

            try {
                List<TextMessage> list = mapper.readValue(json, javaType);
                if (list != null && list.size() > 0) {
                    for (TextMessage message : list)
                        onMessage(message);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
