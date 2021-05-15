package indi.kurok1.http.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

/**
 * 客户端访问http server工具
 * 由命令行参数指定路径
 * 可以指定的参数：
 *  <ul>
 *      <li>maxTimeoutMills[3000],请求最大超时时间</li>
 *      <li>maxConcurrentRequests[10]，最大的并发请求数</li>
 *  </ul>
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class OkHttpClientDemo {
    public static void main(String[] args) {

        HttpTask httpTask = new HttpTask.Builder()
                .url("http://localhost:8081")
                .header("Content-Type", "text/html;charset=utf-8")
                .method("GET")
                .onSuccess(
                        response -> {
                            try {
                                String body = response.body().string();
                                System.out.println(body);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                ).build();
        NetworkEngine.INSTANCE.addTask(httpTask);
    }
}
