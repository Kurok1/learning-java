package indi.kurok1.rpcfx.demo.provider;

import indi.kurok1.rpcfx.demo.api.Order;
import indi.kurok1.rpcfx.demo.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
