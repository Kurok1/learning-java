package indi.kurok1.starter.autoconfigure;

import indi.kurok1.starter.domain.Classroom;
import indi.kurok1.starter.domain.School;
import indi.kurok1.starter.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 学生自动装配，随机分配教室
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Configuration
public class StudentAutoConfiguration {

    private final List<Classroom> classrooms;

    private final School school;

    public StudentAutoConfiguration(List<Classroom> classrooms, School school) {
        this.classrooms = classrooms;
        if (CollectionUtils.isEmpty(this.classrooms))
            throw new IllegalArgumentException("no classroom found");
        this.school = school;
    }

    private final Random random = new Random(System.currentTimeMillis());

    private Student buildStudent(long id, String name, int age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        student.setSchoolId(this.school.getId());
        int index = 0;
        if (this.classrooms.size() > 1)
           index  = random.nextInt(classrooms.size());
        student.setClassroomId(this.classrooms.get(index).getId());
        return student;
    }

    @Bean
    public Student student01() {
        return buildStudent(1L, "student01", 10);
    }

    @Bean
    public Student student02() {
        return buildStudent(2L, "student02", 10);
    }

    @Bean
    public Student student03() {
        return buildStudent(3L, "student03", 10);
    }

    @Bean
    public Student student04() {
        return buildStudent(4L, "student04", 10);
    }

    @Bean
    public Student student05() {
        return buildStudent(5L, "student05", 10);
    }
}
