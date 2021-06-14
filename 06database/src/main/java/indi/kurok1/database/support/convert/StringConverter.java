package indi.kurok1.database.support.convert;

import indi.kurok1.database.support.Converter;

import java.util.Map;

/**
 * 默认转换器，将数据库的值作为String返回
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public class StringConverter implements Converter {

    @Override
    public boolean isSupport(Class<?> clazz) {
        return true;
    }

    @Override
    public Object convert(String target, Class<?> clazz) {
        return target;
    }
}
