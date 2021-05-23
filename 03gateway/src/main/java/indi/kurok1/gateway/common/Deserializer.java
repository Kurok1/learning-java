package indi.kurok1.gateway.common;

/**
 * 反序列化
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public interface Deserializer {

    <T> T decode(byte[] sources, Class<T> clazz);

}
