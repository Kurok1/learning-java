package indi.kurok1.rpcfx.client;

import java.lang.annotation.*;

/**
 * 标记一个接口为远程调用服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RemoteService {

    String serviceName();

    String url();

}
