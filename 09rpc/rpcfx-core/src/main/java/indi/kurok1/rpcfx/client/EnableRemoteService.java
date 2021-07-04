package indi.kurok1.rpcfx.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启远程服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RemoteServiceRegistrar.class)
public @interface EnableRemoteService {

    String[] baskPackages();

}
