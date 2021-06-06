package indi.kurok1.gateway.outbound.netty;

import indi.kurok1.gateway.common.*;
import indi.kurok1.gateway.common.HttpMethod;
import indi.kurok1.gateway.filter.HttpResponseFilter;
import indi.kurok1.gateway.outbound.HttpClient;
import indi.kurok1.gateway.route.ServiceInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * http客户端 netty实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class NettyHttpClient extends HttpClient {

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final Serializer serializer;
    private final Deserializer deserializer;
    private final Iterable<HttpResponseFilter> filters;

    public NettyHttpClient(Serializer serializer, Deserializer deserializer, Iterable<HttpResponseFilter> filters) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.filters = filters;
    }

    @Override
    protected void execute(ServiceInstance serviceInstance, String uri, HttpMethod method, Object requestBody, Map<String, String> httpHeaders) {
        io.netty.handler.codec.http.HttpMethod httpMethod = convert(method);
        if (httpMethod == null)
            throw new IllegalArgumentException("illegal http method");
        WrappedRequest request = new WrappedRequest(serviceInstance, uri, httpMethod, httpHeaders, requestBody, super.context);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new HttpResponseDecoder());
//                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024*1024));
                    ch.pipeline().addLast(new HttpContentDecompressor());
                    ch.pipeline().addLast(new HttpClientHandler(request, serializer, deserializer, filters));
                }
            });


        try {
            ChannelFuture f = bootstrap.connect(serviceInstance.getIpAddress(), serviceInstance.getPort()).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static io.netty.handler.codec.http.HttpMethod convert(HttpMethod method) {
        switch (method) {
            case GET:return io.netty.handler.codec.http.HttpMethod.GET;
            case POST:return io.netty.handler.codec.http.HttpMethod.POST;
            case PUT:return io.netty.handler.codec.http.HttpMethod.PUT;
            case DELETE:return io.netty.handler.codec.http.HttpMethod.DELETE;
        }
        return null;
    }



}
