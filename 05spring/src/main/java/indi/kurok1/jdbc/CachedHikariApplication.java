package indi.kurok1.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 挑战: 基于 AOP 和自定义注解，实现 @MyCache(60) 对于指定方法返回值缓存 60 秒。
 *
 * 这里依赖Spring Boot
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@SpringBootApplication(scanBasePackages = {"indi.kurok1.jdbc"}, exclude = DataSourceAutoConfiguration.class)//因为模块中依赖了spring-boot-starter-jdbc，但是这里没有使用到，避免报错，移除
public class CachedHikariApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachedHikariApplication.class, args);
    }

}
