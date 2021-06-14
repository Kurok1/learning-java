package indi.kurok1.database.support;

import java.util.Map;

/**
 * 字段类型转换器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public interface Converter {

    /**
     * 是否支持转换成当前类对象
     * @param clazz 转换的类对象
     * @return 是否支持
     */
    public boolean isSupport(Class<?> clazz);

    /**
     * 将数据库中读取的值，转换成JAVA类对象
     * @param target 数据库中的值
     * @param clazz JAVA类对象
     * @return 返回转换后的值，可以为null
     */
    public Object convert(String target, Class<?> clazz);

    /**
     * 初始化参数
     * @param params 参数，非空
     */
    default void init(Map<String, String> params) {};

}
