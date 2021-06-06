package indi.kurok1.jdbc.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 在JVM内存中缓存数据
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Component
public class MemoryCacheProvider implements CacheAbility {


    private final ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>(256);
    private final ConcurrentHashMap<String, Long> expiredMap = new ConcurrentHashMap<>(256);

    @Override
    public Object getResultFromCache(String methodName, Object[] args) {
        try {
            String cacheKey = getCacheKey(methodName, args);
            //检查是否过期
            if (isExpired(methodName, args))
                return null;

            Object result = resultMap.getOrDefault(cacheKey, null);
            //更新缓存
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void store(String methodName, int expired, Object result, Object[] args) {
        String cacheKey = getCacheKey(methodName, args);
        expiredMap.put(cacheKey, System.currentTimeMillis() + expired);
        resultMap.put(cacheKey, result);
    }

    @Override
    public boolean isExpired(String methodName, Object[] args) {
        String cacheKey = getCacheKey(methodName, args);
        Long expired = expiredMap.get(cacheKey);
        if (expired == null)
            return true;
        return System.currentTimeMillis() >= expired;
    }
}
