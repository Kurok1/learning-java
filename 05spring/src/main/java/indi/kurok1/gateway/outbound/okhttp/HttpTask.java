package indi.kurok1.gateway.outbound.okhttp;

import indi.kurok1.gateway.common.HttpMethod;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * http请求任务封装
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public abstract class HttpTask implements Callable<Response> {

    private OkHttpClient httpClient;

    private Request request;

    public HttpTask(OkHttpClient httpClient, Request request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public Response call() throws Exception {
        Call call = httpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            onSuccess(response);
            return response;
        } catch (IOException e) {
            onError(e);
        }
        return response;
    }

    /**
     * 异常触发
     * @param e
     */
    public abstract void onError(Exception e);

    /**
     * 调用成功，不涉及状态码的校验
     * @param response
     */
    public abstract void onSuccess(Response response);

    /**
     * 整个调用流程结束
     */
    public void onFinish() {}


    public static class Builder {
        private int maxTimeoutMills = Integer.parseInt(System.getProperty("maxTimeoutMills", "3000"));
        private OkHttpClient httpClient = null;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private HttpMethod method;
        private RequestBody requestBody;

        public Builder httpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.putIfAbsent(key, value);
            return this;
        }

        public Builder header(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder requestBody(RequestBody body) {
            this.requestBody = body;
            return this;
        }

        private Consumer<Response> successConsumer;
        private Consumer<Exception> errorConsumer;

        public Builder onSuccess(Consumer<Response> consumer) {
            this.successConsumer = consumer;
            return this;
        }

        public Builder onError(Consumer<Exception> consumer) {
            this.errorConsumer = consumer;
            return this;
        }

        public HttpTask build() {
            if (httpClient == null)
                httpClient = new OkHttpClient.Builder().callTimeout(maxTimeoutMills, TimeUnit.MILLISECONDS).build();
            if (url == null || url.length() < 4) {
                throw new IllegalArgumentException("url is illegal!");
            }
            if (method == null) {
                throw new IllegalArgumentException("method is required!");
            }
            Request.Builder builder = null;
            switch (this.method) {
                case GET:builder = new Request.Builder().get();break;
                case POST:builder = new Request.Builder().post(this.requestBody == null ? RequestBody.create("", null) : this.requestBody);break;
                case DELETE:builder = new Request.Builder().delete();break;
                case PUT:builder = new Request.Builder().put(this.requestBody == null ? RequestBody.create("", null) : this.requestBody);break;
                default:throw new IllegalArgumentException(String.format("unsupported http request method : [%s]", this.method));
            }
            Request request = builder.url(this.url).build();
            for (Map.Entry<String, String> header : headers.entrySet())
                builder.addHeader(header.getKey(), header.getValue());
            return new HttpTask(this.httpClient, builder.build()) {
                @Override
                public void onError(Exception e) {
                    if (errorConsumer != null)
                        errorConsumer.accept(e);
                }

                @Override
                public void onSuccess(Response response) {
                    try {
                        if (successConsumer != null)
                            successConsumer.accept(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    //释放资源
                    httpClient = null;
                }
            };
        }
    }
}
