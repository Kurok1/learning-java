package indi.kurok1.gateway.common;

/**
 * 序列化
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public interface Serializer {

    byte[] encode(Object target);

}
