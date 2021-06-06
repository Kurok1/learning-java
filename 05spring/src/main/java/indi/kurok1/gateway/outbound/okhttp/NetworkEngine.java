package indi.kurok1.gateway.outbound.okhttp;

import okhttp3.Response;

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

    /**
     * 异步执行提交任务核心方法
     * @param httpTask 需要提交的http请求任务
     */
    public void asyncRun(HttpTask httpTask) {
        if (this.executorService.isShutdown())
            throw new RuntimeException("the executorService is shutdown!");
        this.executorService.submit(httpTask);
    }

}
