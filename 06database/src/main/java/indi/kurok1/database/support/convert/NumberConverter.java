package indi.kurok1.database.support.convert;

import indi.kurok1.database.support.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Long类型转换
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public class NumberConverter implements Converter {

    /**
     * 是否支持转换成当前类对象
     *
     * @param clazz 转换的类对象
     * @return 是否支持
     */
    @Override
    public boolean isSupport(Class<?> clazz) {
        return clazz.isPrimitive() || Number.class.isAssignableFrom(clazz);
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
        switch (clazz.getName()) {
            case "java.lang.Integer":
            case "int":
                return Integer.parseInt(target);
            case "java.lang.Long":
            case "long":
                return Long.parseLong(target);
            case "java.math.BigDecimal": return new BigDecimal(target);
            case "java.math.BigInteger": return new BigInteger(target);
            case "java.lang.Double":
            case "double":
                return Double.parseDouble(target);
            case "java.lang.Float":
            case "float":
                return Float.parseFloat(target);
            case "java.lang.Short":
            case "short":
                return Short.parseShort(target);
            default:return target;
        }
    }
}
