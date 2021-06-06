package indi.kurok1.spring.configuration;

import indi.kurok1.spring.bean.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置项
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.05
 */
@Configuration
public class SpringConfiguration {

    @Bean("student_bean")
    public Student student() {
        return new Student();
    }

}
