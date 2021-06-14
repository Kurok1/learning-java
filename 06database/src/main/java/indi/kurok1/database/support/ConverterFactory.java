package indi.kurok1.database.support;

import java.util.Map;

/**
 * 转换器工厂
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public class ConverterFactory {

    public static Converter getConverter(Class<? extends Converter> clazz, Map<String, String> initParam) {
        try {
            Converter converter = clazz.newInstance();
            converter.init(initParam);
            return converter;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
