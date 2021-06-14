package indi.kurok1.database.annoation;

import java.lang.annotation.*;

/**
 * 标记一个字段为ud
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Id {
}
