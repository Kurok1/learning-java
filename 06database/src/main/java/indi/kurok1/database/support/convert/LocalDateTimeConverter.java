package indi.kurok1.database.support.convert;

import indi.kurok1.database.support.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 日期时间转换
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public class LocalDateTimeConverter implements Converter {


    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_KEY = "DATE_FORMAT";

    private String format = DEFAULT_DATE_TIME_FORMAT;

    /**
     * 是否支持转换成当前类对象
     *
     * @param clazz 转换的类对象
     * @return 是否支持
     */
    @Override
    public boolean isSupport(Class<?> clazz) {
        return LocalDateTime.class == clazz;
    }

    /**
     * 将数据库中读取的值，转换成JAVA类对象
     *
     * @param target 数据库中的值
     * @param clazz  JAVA类对象
     * @return 返回转换后的值，可以为null
     */
    @Override
    public Object convert(String target, Class<?> clazz) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(target, formatter);
    }

    /**
     * 初始化参数
     *
     * @param params 参数，非空
     */
    @Override
    public void init(Map<String, String> params) {
        if (params.containsKey(FORMAT_KEY)) {
            format = params.get(FORMAT_KEY);
        }
    }
}
