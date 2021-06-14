package indi.kurok1.database.annoation;

import indi.kurok1.database.support.Converter;
import indi.kurok1.database.support.convert.StringConverter;

import java.lang.annotation.*;

/**
 * 字段名称
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 * @see Converter
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Column {

    String name() default "";

    /**
     * 指定转换器
     * @return
     */
    Class<? extends Converter> converter() default StringConverter.class;

    ConvertParam[] convertParams() default {};

}
