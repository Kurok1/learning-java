package indi.kurok1.starter.controller;

import indi.kurok1.starter.domain.School;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 学校访问
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@RequestMapping("api/school")
@ResponseBody
public class SchoolController {

    private final School school;

    public SchoolController(School school) {
        this.school = school;
    }

    @GetMapping("/")
    public School get() {
        return this.school;
    }
}
