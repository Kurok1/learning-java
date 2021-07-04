package indi.kurok1.rpcfx.demo.api;

import indi.kurok1.rpcfx.client.RemoteService;

@RemoteService(serviceName = "indi.kurok1.rpcfx.demo.api.OrderService", url = "http://localhost:8080/")
public interface OrderService {

    Order findOrderById(int id);

}
