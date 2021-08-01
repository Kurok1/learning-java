package indi.kurok1.kmq.client.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 客户端配置
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@ConfigurationProperties(prefix = "kmq.client")
public class KmqClientProperties {

    private String serverAddress;

    private int port;

    private List<String> queues = new LinkedList<>();

    private Map<String, Integer> queueLimits = new HashMap<>();

    private boolean autoCommit = true;

    private int defaultFetchSize = 10;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, Integer> getQueueLimits() {
        return queueLimits;
    }

    public void setQueueLimits(Map<String, Integer> queueLimits) {
        this.queueLimits = queueLimits;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public int getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(int defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }
}
