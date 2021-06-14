package indi.kurok1.database.controller;

import indi.kurok1.database.domain.User;
import indi.kurok1.database.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户访问
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userService) {
        this.userRepository = userService;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id) {
        return this.userRepository.getById(id);
    }

    @GetMapping("/")
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @PostMapping("/save")
    public User save(@RequestBody User user) {
        return this.userRepository.insert(user);
    }

}
