package indi.kurok1.gateway.outbound;

import indi.kurok1.gateway.common.HttpMethod;
import indi.kurok1.gateway.route.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * http客户端抽象实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public abstract class HttpClient {

    protected ChannelHandlerContext context;

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    /**
     * 执行请求的核心操作，封装由实现类实现
     * @param uri 请求uri，not null
     * @param method 请求方法, not null
     * @param requestBody 请求体, 允许为null
     * @param httpHeaders http请求头，允许为null
     */
    protected abstract void execute(ServiceInstance serviceInstance, String uri, HttpMethod method, Object requestBody, Map<String, String> httpHeaders);

    public void get(ServiceInstance serviceInstance, String uri, Map<String, String> httpHeaders, ChannelHandlerContext context) {
        setContext(context);
        execute(serviceInstance, uri, HttpMethod.GET, null, httpHeaders);
    }

    public void post(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context) {
        setContext(context);
        execute(serviceInstance, uri, HttpMethod.POST, requestBody, httpHeaders);
    }

    public void put(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context) {
        setContext(context);
        execute(serviceInstance, uri, HttpMethod.PUT, requestBody, httpHeaders);
    }

    public void delete(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context) {
        setContext(context);
        execute(serviceInstance, uri, HttpMethod.DELETE, requestBody, httpHeaders);
    }

}
