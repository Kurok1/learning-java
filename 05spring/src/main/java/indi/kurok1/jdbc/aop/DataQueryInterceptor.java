package indi.kurok1.jdbc.aop;

import indi.kurok1.jdbc.annoation.MyCache;
import indi.kurok1.jdbc.cache.CacheAbility;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP拦截
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Aspect
@Component
public class DataQueryInterceptor {

    private final CacheAbility cacheProvider;

    @Autowired
    public DataQueryInterceptor(CacheAbility cacheAbility) {
        this.cacheProvider = cacheAbility;
    }

    private static final Logger logger = LoggerFactory.getLogger(DataQueryInterceptor.class);

    private Class<MyCache> getAnnotation() { return MyCache.class; }

    @Around(value = "execution(* indi.kurok1.jdbc.service.*Service.*(..))")
    public Object before(ProceedingJoinPoint joinPoint) throws RuntimeException {
        Object[] args = joinPoint.getArgs();//参数列表
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(getAnnotation())) {
            MyCache myCache = method.getAnnotation(getAnnotation());
            //获取超时时长
            int expired = myCache.expiredMillions();
            String methodName = method.getDeclaringClass().getName() + "#" + method.getName();
            if (cacheProvider.isExpired(methodName, args)) {
                try {
                    Object result = joinPoint.proceed(args);
                    //存储
                    cacheProvider.store(methodName, expired, result, args);
                    return result;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                logger.info("fetch result from cache");
                return cacheProvider.getResultFromCache(methodName, args);
            }
        } else {
            //不操作
                try {
                    if (args == null || args.length == 0) {
                        return joinPoint.proceed();
                    } else return joinPoint.proceed(args);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
        }
        return null;
    }

}
