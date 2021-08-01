package indi.kurok1.kmq.client;

import indi.kurok1.kmq.client.autoconfig.KmqClientProperties;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 队列管理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public class QueueAdmin {

    private final RestTemplate restTemplate;
    private final KmqClientProperties properties;

    private final String CREATED_PATH = "/api/queue/create";
    private final String DELETE_PATH = "/api/queue/delete";

    public QueueAdmin(RestTemplate restTemplate, KmqClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;

        if (properties.getQueues().size() > 0) {
            for (String queue : properties.getQueues())
                createQueue(queue);
        }

    }


    /**
     * 创建队列
     * @param queueName 队列名称
     * @throws RestClientException 调用失败
     */
    public void createQueue(String queueName) throws RestClientException {
        String url = String.format("http://%s:%d%s/%s", this.properties.getServerAddress(), this.properties.getPort(), this.CREATED_PATH, queueName);
        this.restTemplate.postForEntity(url, null, String.class);
    }

    /**
     * 销毁队列
     * @param queueName 队列名称
     * @throws RestClientException 调用失败
     */
    public void deleteQueue(String queueName) throws RestClientException {
        String url = String.format("http://%s:%d%s/%s", this.properties.getServerAddress(), this.properties.getPort(), this.DELETE_PATH, queueName);
        this.restTemplate.delete(url);
    }
}
