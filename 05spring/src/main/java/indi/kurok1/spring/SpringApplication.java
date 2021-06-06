package indi.kurok1.spring;

import indi.kurok1.spring.bean.Student;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * Spring主应用
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.05
 */
public class SpringApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-context.xml");
        //使用BeanFactory动态注册Bean
        Student student = new Student();
        context.getBeanFactory().registerSingleton("student_factory", student);

        System.out.println(Arrays.asList(context.getBeanFactory().getBeanNamesForType(Student.class)));
        //[student_component, student_xml, student_bean, student_factory]

    }

}
