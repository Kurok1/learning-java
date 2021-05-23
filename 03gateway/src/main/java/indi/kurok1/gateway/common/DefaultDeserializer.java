package indi.kurok1.gateway.common;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 默认序列化器，使用JDK默认反序列化方式
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.22
 */
public class DefaultDeserializer implements Deserializer {

    @Override
    public <T> T decode(byte[] sources, Class<T> clazz) {
        if (clazz == String.class)
            return (T) new String(sources, StandardCharsets.UTF_8);
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(sources));
            return (T) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
