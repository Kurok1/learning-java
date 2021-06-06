package indi.kurok1.gateway.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启响应过滤
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RequestFilterSelector.class})
public @interface EnableResponseFilter {
}
