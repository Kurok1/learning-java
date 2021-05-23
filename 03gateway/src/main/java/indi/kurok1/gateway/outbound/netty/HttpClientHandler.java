package indi.kurok1.gateway.outbound.netty;

import indi.kurok1.gateway.common.Deserializer;
import indi.kurok1.gateway.common.Serializer;
import indi.kurok1.gateway.common.ServiceUtils;
import indi.kurok1.gateway.filter.HttpResponseFilter;
import indi.kurok1.gateway.outbound.DefaultErrorResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Netty客户端处理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private WrappedRequest request;
    private Serializer serializer;
    private Deserializer deserializer;
    private Iterable<HttpResponseFilter> filters;

    public HttpClientHandler(WrappedRequest request, Serializer serializer, Deserializer deserializer) {
        this.request = request;
        this.serializer = serializer;
        this.deserializer = deserializer;
        filters = ServiceUtils.allImplement(HttpResponseFilter.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(toHttpRequest(request));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse && request.getContext() != null) {
            FullHttpResponse response = (FullHttpResponse) msg;
            //过滤
            if (filters != null) {
                for (HttpResponseFilter filter : filters) {
                    boolean needFilter = filter.filter(response, ctx);
                    if (!needFilter)
                        break;
                }

            }

            String value = response.content().toString(StandardCharsets.UTF_8);
            FullHttpResponse res = new DefaultFullHttpResponse(response.protocolVersion(), response.status(), Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8)));
            for (Map.Entry<String, String> header : response.headers().entries()) {
                res.headers().add(header.getKey(), header.getValue());
            }
            ChannelHandlerContext context = request.getContext();
            context.writeAndFlush(res);
            ctx.fireChannelRead(msg);
            ctx.disconnect().addListener(ChannelFutureListener.CLOSE);

        } else super.channelRead(ctx, msg);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (request.getContext() != null) {
            request.getContext().writeAndFlush(DefaultErrorResponse.INTERNAL_SERVER_ERROR_500);
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    public FullHttpRequest toHttpRequest(WrappedRequest request) {
        HttpVersion version = HttpVersion.HTTP_1_1;
        ByteBuf byteBuf = null;
        if (request.getRequestBody() instanceof String) {
            byteBuf = Unpooled.wrappedBuffer(((String) request.getRequestBody()).getBytes(StandardCharsets.UTF_8));
        } else if (this.serializer != null)
            byteBuf = Unpooled.wrappedBuffer(this.serializer.encode(request.getRequestBody()));
        FullHttpRequest httpRequest = null;
        if (byteBuf == null)
            httpRequest = new DefaultFullHttpRequest(version, request.getMethod(), request.getUri());
        else httpRequest = new DefaultFullHttpRequest(version, request.getMethod(), request.getUri(), byteBuf);
        httpRequest.headers().add(HttpHeaderNames.HOST, request.getServiceInstance().getIpAddress());
        httpRequest.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (byteBuf != null)
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.capacity());
        return httpRequest;
    }
}
