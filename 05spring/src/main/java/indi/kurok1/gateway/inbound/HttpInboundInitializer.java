package indi.kurok1.gateway.inbound;

import indi.kurok1.gateway.filter.HttpRequestFilter;
import indi.kurok1.gateway.route.Dispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
@Component
public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

    private final Dispatcher dispatcher;
    private final List<HttpRequestFilter> filters;

    public HttpInboundInitializer(Dispatcher dispatcher, List<HttpRequestFilter> filters) {
        this.dispatcher = dispatcher;
        this.filters = filters;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
//		if (sslCtx != null) {
//			p.addLast(sslCtx.newHandler(ch.alloc()));
//		}
        p.addLast(new HttpServerCodec());
        //p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        p.addLast(new HttpInboundHandler(dispatcher, filters));
    }
}
