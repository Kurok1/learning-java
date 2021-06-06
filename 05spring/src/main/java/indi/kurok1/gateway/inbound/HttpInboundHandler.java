package indi.kurok1.gateway.inbound;

import indi.kurok1.gateway.outbound.DefaultErrorResponse;
import indi.kurok1.gateway.route.Dispatcher;
import indi.kurok1.gateway.common.ServiceUtils;
import indi.kurok1.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final Dispatcher dispatcher;
    //filter只读
    private final Iterable<HttpRequestFilter> filters;


    public HttpInboundHandler(Dispatcher dispatcher, Iterable<HttpRequestFilter> filters) {
        this.dispatcher = dispatcher;
        this.filters = filters;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

            //服务实例由aop注入
            this.dispatcher.dispatch(fullRequest, ctx, null, null);


        } catch(Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(DefaultErrorResponse.INTERNAL_SERVER_ERROR_500);
            ctx.close().syncUninterruptibly();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }



}
