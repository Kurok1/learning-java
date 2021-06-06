package indi.kurok1.starter;

import indi.kurok1.starter.autoconfigure.SchoolAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 必做2 自动装配和自定义starter
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//因为模块中依赖了spring-boot-starter-jdbc，但是这里没有使用到，避免报错，移除
@Import(SchoolAutoConfiguration.class)
public class SpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterApplication.class, args);
    }

}
