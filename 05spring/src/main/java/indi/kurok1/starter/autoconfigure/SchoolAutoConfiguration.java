package indi.kurok1.starter.autoconfigure;

import indi.kurok1.starter.controller.SchoolController;
import indi.kurok1.starter.domain.School;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 学校自动装配
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@EnableConfigurationProperties(School.class)
@ConditionalOnClass(School.class)
public class SchoolAutoConfiguration {

    @Bean
    public SchoolController schoolController(School school) {
        return new SchoolController(school);
    }

}
