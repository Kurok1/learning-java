package indi.kurok1.rpcfx.http;

import com.alibaba.fastjson.JSON;
import indi.kurok1.rpcfx.api.RpcfxRequest;
import indi.kurok1.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Netty实现的http client
 *
 * @author <a href="mailto:chan@ittx.com.cn">韩超</a>
 * @version 2021.07.05
 */
public class NettyHttpClient extends HttpClient {
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    public RpcfxResponse post(RpcfxRequest req, String url) {


        try {
            final AtomicReference<RpcfxResponse> reference = new AtomicReference<>(null);
            final CountDownLatch latch = new CountDownLatch(1);
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024*1024));
                    ch.pipeline().addLast(new HttpContentDecompressor());
                    ch.pipeline().addLast(new HttpClientInboundHandler(latch, reference));
                }
            });

            URI uri = new URI(url);
            // Start the client.
            ChannelFuture f = b.connect(uri.getHost(), uri.getPort()).sync();

            String msg = JSON.toJSONString(req);
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

            // 构建http请求
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            // 发送http请求
            f.channel().write(request);
            f.channel().flush();
            f.channel().closeFuture().sync();

            latch.await(30, TimeUnit.SECONDS);
            return reference.get();
        } catch (Exception e) {
            return null;
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {
        private final CountDownLatch latch;
        private final AtomicReference<RpcfxResponse> reference;

        public HttpClientInboundHandler(CountDownLatch latch, AtomicReference<RpcfxResponse> reference) {
            this.latch = latch;
            this.reference = reference;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                //todo 过滤


                String value = response.content().toString(StandardCharsets.UTF_8);
                System.out.println("resp json: "+value);
                reference.set(JSON.parseObject(value, RpcfxResponse.class));
                latch.countDown();
                ctx.fireChannelRead(msg);
                ctx.disconnect().addListener(ChannelFutureListener.CLOSE);

            } else super.channelRead(ctx, msg);
        }
    }
}
