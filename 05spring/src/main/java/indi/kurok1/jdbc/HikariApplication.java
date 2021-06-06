package indi.kurok1.jdbc;

import indi.kurok1.jdbc.service.HikariJdbcService;
import indi.kurok1.jdbc.service.JdbcService;

import java.util.Arrays;
import java.util.List;

/**
 * 整合Hikari
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
public class HikariApplication {

    private final static Student student = Student.of("Hikari", 11);

    public static void main(String[] args) {
        JdbcService jdbcService = new HikariJdbcService();

        //新增
        Student result = jdbcService.insert(student);
        System.out.println(result);

        //删除
        int updated = jdbcService.delete(2);
        if (updated > 0)
        System.out.println("删除成功");
        else System.out.println("删除失败");

        //更新
        student.setName("new Name");
        student.setId(3L);
        updated = jdbcService.update(student);
        if (updated > 0)
        System.out.println("更新成功");
        else System.out.println("更新失败");

        //查找
        List<Student> students = jdbcService.findAll();
        System.out.println(students);

        for (Student student : students)
            student.setName("Hikari1");

        int[] effects = jdbcService.batchUpdate(students);
        System.out.print("执行影响行数:");
        System.out.println(Arrays.stream(effects).reduce(Integer::sum).orElse(0));
    }

}
