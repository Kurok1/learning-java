package indi.kurok1.jdbc.cache;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 缓存接口
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
public interface CacheAbility {

    final static String[] strHex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };


    default String getCacheKey(String methodName, Object[] args) throws RuntimeException {
        //采用MD5码的方式,生成缓存key
        //利用每个参数的hashCode,组合在一起,生成MD5,即为缓存key
        StringBuffer hashCode = new StringBuffer();
        hashCode.append(methodName);
        for (Object arg : args)
            hashCode.append(String.valueOf(arg.hashCode()));

        StringBuffer key = new StringBuffer();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] b = md.digest(hashCode.toString().getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < b.length; i++) {
            int d = b[i];
            if (d < 0) {
                d += 256;
            }
            int d1 = d / 16;
            int d2 = d % 16;
            key.append(strHex[d1]).append(strHex[d2]);
        }
        return key.toString();
    }

    /**
     * 根据指定的参数,从缓存中获取数据,该操作并不会延长缓存超时时间
     * @param methodName 方法名称
     * @param args 参数
     * @return 返回缓存中缓存的结果
     */
    Object getResultFromCache(String methodName, Object[] args);

    /**
     * 存储结果
     * @param methodName 方法名称
     * @param expired 过期时间
     * @param result 缓存的值
     * @param args 参数
     */
    void store(String methodName, int expired, Object result, Object[] args);

    /**
     * 判断是否过期
     * @param methodName 方法名称
     * @param args 参数
     * @return 是否已过期
     */
    boolean isExpired(String methodName, Object[] args);

}
