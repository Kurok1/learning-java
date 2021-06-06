package indi.kurok1.jdbc.annoation;

import java.lang.annotation.*;

/**
 * 指定方法缓存,默认保留60s
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyCache {

    int expiredMillions() default 60 * 1000;

}
