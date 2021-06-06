package indi.kurok1.gateway.common;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 服务实现工具
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 * @see ServiceLoader
 * @Deprecated 改用Spring注入
 */
@Deprecated
public class ServiceUtils {

    public static <S> S defaultImplement(Class<S> requiredType) {
        Iterable<S> serviceLoader = allImplement(requiredType);
        Iterator<S> iterator = serviceLoader.iterator();
        //取第一个
        if (!iterator.hasNext())
            throw new IllegalArgumentException("no default implement found : " + requiredType.getName());
        return iterator.next();
    }

    public static <S> S defaultImplement(Class<S> requiredType, S defaultService) {
        Iterable<S> serviceLoader = allImplement(requiredType);
        Iterator<S> iterator = serviceLoader.iterator();
        //找第一个，没有返回默认实现
        if (!iterator.hasNext())
            return defaultService;
        return iterator.next();
    }

    public static <S> Iterable<S> allImplement(Class<S> requiredType) {
        //返回所有实现
        return ServiceLoader.load(requiredType, ServiceUtils.class.getClassLoader());
    }


}
