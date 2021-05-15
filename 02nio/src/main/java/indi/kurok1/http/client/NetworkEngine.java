package indi.kurok1.http.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * http请求任务队列
 * 队列实现，最大容量由maxConcurrentRequests控制
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class NetworkEngine {

    private int maxConcurrentRequests = Integer.parseInt(System.getProperty("maxConcurrentRequests", "10"));

    private ExecutorService executorService = Executors.newFixedThreadPool(maxConcurrentRequests);

    public static NetworkEngine INSTANCE = new NetworkEngine();

    private NetworkEngine() {

    }

    public void addTask(HttpTask httpTask) {
        if (this.executorService.isShutdown())
            throw new RuntimeException("the executorService is shutdown!");
        this.executorService.submit(httpTask);
    }

}
