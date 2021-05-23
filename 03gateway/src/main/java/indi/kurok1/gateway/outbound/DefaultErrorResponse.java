package indi.kurok1.gateway.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

/**
 * 错误消息返回模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.23
 */
public class DefaultErrorResponse {

    public static final DefaultFullHttpResponse INTERNAL_SERVER_ERROR_500 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.INTERNAL_SERVER_ERROR,
            Unpooled.wrappedBuffer("INTERNAL SERVER ERROR".getBytes(StandardCharsets.UTF_8)));
    public static final DefaultFullHttpResponse INTERNAL_SERVER_ERROR_404 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.NOT_FOUND,
            Unpooled.wrappedBuffer("NOT FOUND".getBytes(StandardCharsets.UTF_8)));

}
