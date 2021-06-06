package indi.kurok1.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 给请求方的http请求添加令牌
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.23
 */
public class TokenHttpRequestFilter implements HttpRequestFilter {

    private static final String tokenHeader = "X-TOKEN";

    @Override
    public boolean filter(HttpRequest request, ChannelHandlerContext ctx) {
        request.headers().add(tokenHeader, generateToken());
        return true;
    }

    /**
     * 产生token
     * @return 返回当前时间戳的base64码
     */
    private String generateToken() {
        return String.valueOf(Base64.getEncoder().encode(Long.toString(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)));
    }
}
