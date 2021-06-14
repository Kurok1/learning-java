package indi.kurok1.database.support;

import indi.kurok1.database.annoation.ConvertParam;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 字段元数据
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Getter
public class ColumnMetaData {

    /**
     * java对象属性名称
     */
    @Setter
    private String property;

    /**
     * 数据库中字段名称
     */
    @Setter
    private String columnName;

    /**
     * java类对象属性类型
     */
    @Setter
    private Class<?> propertyType;

    @Setter
    private Method setter;

    @Setter
    private Method getter;

    private final Converter converter;

    private final ConvertParam[] convertParams;

    public ColumnMetaData(ConvertParam[] convertParams, Class<? extends Converter> clazz) {
        this.convertParams = convertParams;
        final Map<String, String> params = new HashMap<>();
        if (convertParams != null && convertParams.length > 0) {
            for (ConvertParam param : convertParams) {
                String key = param.key();
                String value = param.value();
                params.put(key, value);
            }
        }
        this.converter = ConverterFactory.getConverter(clazz, params);
    }
}
