package indi.kurok1.datasource.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 分库分表测试
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.20
 */
@RestController
@RequestMapping("api/test02")
public class ShardingTestController {

    private final JdbcTemplate jdbcTemplate;

    public ShardingTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostMapping("insert")
    public String insert() {
        this.jdbcTemplate.update("insert into student(`name`, `age`) values ('name', 13)");
        return "OK";
    }

    @GetMapping("/read")
    public List<Map<String, Object>> read() {
        return this.jdbcTemplate.queryForList("select * from student");
    }
}
