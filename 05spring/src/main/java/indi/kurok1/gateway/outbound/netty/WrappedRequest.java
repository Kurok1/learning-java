package indi.kurok1.gateway.outbound.netty;

import indi.kurok1.gateway.route.ServiceInstance;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

/**
 * 向服务方发送请求的包装
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class WrappedRequest {

    private final ServiceInstance serviceInstance;
    private final String uri;
    private final HttpMethod method;
    private final Map<String, String> headers;
    private final Object requestBody;
    private final ChannelHandlerContext context;

    public WrappedRequest(ServiceInstance serviceInstance,
                          String uri,
                          HttpMethod method,
                          Map<String, String> headers,
                          Object requestBody,
                          ChannelHandlerContext context) {
        this.serviceInstance = serviceInstance;
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        this.requestBody = requestBody;
        this.context = context;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }


}
