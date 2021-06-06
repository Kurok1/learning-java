package indi.kurok1.gateway.aop;

import indi.kurok1.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 请求过滤aop实现
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Aspect
@Component
public class FilterInterceptor {

    private final List<HttpRequestFilter> filterList;

    private final static Logger logger = LoggerFactory.getLogger(FilterInterceptor.class);

    public FilterInterceptor(List<HttpRequestFilter> filterList) {
        this.filterList = filterList;
    }

    @Around(value = "execution(* indi.kurok1.gateway.inbound.HttpInboundHandler.channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object))")
    public Object before(ProceedingJoinPoint joinPoint) throws RuntimeException {
        Object[] args = joinPoint.getArgs();
        if (filterList != null && filterList.size() > 0) {
            //过滤请求
            for (HttpRequestFilter filter : filterList) {
                boolean needFilter = filter.filter((FullHttpRequest)args[1], (ChannelHandlerContext)args[0]);
                if (!needFilter)
                    return null;
            }
        }
        try {
            return joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
