package indi.kurok1.gateway.route;

import indi.kurok1.gateway.autoconfigure.GatewayProperties;
import indi.kurok1.gateway.outbound.HttpClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 处理入口请求，并且分发到对应的服务实例上
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
@Component
@Scope("singleton")
public class Dispatcher {


    private final ServiceChooser serviceChooser;
    private final HttpClient httpClient;
    private final GatewayProperties properties;

    public Dispatcher(ServiceChooser serviceChooser, HttpClient httpClient, GatewayProperties properties) {
        this.serviceChooser = serviceChooser;
        this.httpClient = httpClient;
        this.properties = properties;
    }


    /**
     * 分发请求到服务实例
     * @param httpRequest 入口接收的http请求
     * @param context 上下文
     * @param content 发送给服务方的请求数据
     * @param instance 服务实例,aop注入
     */
    public void dispatch(FullHttpRequest httpRequest, ChannelHandlerContext context, Object content, ServiceInstance instance) {
        Map<String, String> headers = new HashMap<>();
        if (httpRequest.headers().size() > 0) {
            httpRequest.headers().forEach(
                    (it)-> headers.put(it.getKey(), it.getValue())
            );
        }

        switch (httpRequest.method().name()) {
            case "GET":
                httpClient.get(instance, httpRequest.uri(), headers, context);break;
            case "POST":
                httpClient.post(instance, httpRequest.uri(), content, headers, context);break;
            case "PUT":
                httpClient.put(instance, httpRequest.uri(), content, headers, context);break;
            case "DELETE":
                httpClient.delete(instance, httpRequest.uri(), content, headers, context);break;
            default:
                throw new IllegalStateException("Unexpected value: " + httpRequest.method().name());
        }
    }



}
