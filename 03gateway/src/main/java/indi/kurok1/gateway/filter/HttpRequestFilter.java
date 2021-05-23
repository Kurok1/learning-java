package indi.kurok1.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public interface HttpRequestFilter {

    /**
     * 过滤请求
     * @param request http请求
     * @param ctx
     * @return 如果返回true，表示继续过滤下去，否则直接关闭流程，请确保返回false时已经写入了响应数据
     */
    boolean filter(HttpRequest request, ChannelHandlerContext ctx);

}
