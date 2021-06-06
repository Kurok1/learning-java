package indi.kurok1.gateway.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * 默认序列化器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
public class DefaultSerializer implements Serializer {


    @Override
    public byte[] encode(Object target) {
        //JSON序列化
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(target);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
