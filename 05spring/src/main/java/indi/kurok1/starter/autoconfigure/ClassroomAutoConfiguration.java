package indi.kurok1.starter.autoconfigure;

import indi.kurok1.starter.domain.Classroom;
import indi.kurok1.starter.domain.School;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * 教室自动生成
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@ConditionalOnClass(Classroom.class)
@AutoConfigureAfter(SchoolAutoConfiguration.class)
public class ClassroomAutoConfiguration {

    private final School school;

    public ClassroomAutoConfiguration(School school) {
        this.school = school;
    }


    @Bean
    public Classroom classroom01() {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("classroom01");
        classroom.setSchoolId(this.school.getId());
        return classroom;
    }

    @Bean
    public Classroom classroom02() {
        Classroom classroom = new Classroom();
        classroom.setId(2L);
        classroom.setName("classroom02");
        classroom.setSchoolId(this.school.getId());
        return classroom;
    }

    @Bean
    public Classroom classroom03() {
        Classroom classroom = new Classroom();
        classroom.setId(3L);
        classroom.setName("classroom03");
        classroom.setSchoolId(this.school.getId());
        return classroom;
    }
}
