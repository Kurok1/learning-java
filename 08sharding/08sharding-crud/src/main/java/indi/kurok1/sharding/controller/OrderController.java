package indi.kurok1.sharding.controller;

import indi.kurok1.sharding.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.27
 */
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/order/insert")
    public int insert() {
        return this.orderService.insert();
    }

    @GetMapping("api/order/select/{id}")
    public List<Map<String, Object>> select(@PathVariable("id") Long id) {
        return this.orderService.findById(id);
    }

    @PutMapping("api/order/update/{id}")
    public int update(@PathVariable("id") Long id) {
        Map<String, Object> map = Collections.singletonMap("code", "newCode");
        return this.orderService.update(map, id);
    }

    @DeleteMapping("api/order/delete/{id}")
    public int delete(@PathVariable("id") Long id) {
        this.orderService.delete(id);
        return 1;
    }
}
