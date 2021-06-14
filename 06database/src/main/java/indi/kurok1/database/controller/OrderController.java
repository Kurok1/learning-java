package indi.kurok1.database.controller;

import indi.kurok1.database.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@RestController
@RequestMapping("api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderService.OrderWrapper getById(@PathVariable("id") Long id) {
        return this.orderService.getOrder(id);
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody OrderService.OrderWrapper wrapper) {
        return this.orderService.save(wrapper);
    }

}
