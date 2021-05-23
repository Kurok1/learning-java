package indi.kurok1.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponse;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public interface HttpResponseFilter {

    boolean filter(HttpResponse response, ChannelHandlerContext ctx);

}
