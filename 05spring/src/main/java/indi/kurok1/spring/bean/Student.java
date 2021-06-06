package indi.kurok1.spring.bean;

import org.springframework.stereotype.Component;

/**
 * 学生实体，需要注册到Spring中
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.05
 */
@Component("student_component")
public class Student {

    private long id = 1;
    private String name = "student";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
