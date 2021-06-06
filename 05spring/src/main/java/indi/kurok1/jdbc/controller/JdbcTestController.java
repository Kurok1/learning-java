package indi.kurok1.jdbc.controller;

import indi.kurok1.jdbc.Student;
import indi.kurok1.jdbc.service.HikariJdbcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 测试
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@RestController
public class JdbcTestController {

    private final HikariJdbcService jdbcService;

    public JdbcTestController(HikariJdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @GetMapping("/api/students")
    public Collection<Student> findAll() {
        return jdbcService.findAll();
    }

}
