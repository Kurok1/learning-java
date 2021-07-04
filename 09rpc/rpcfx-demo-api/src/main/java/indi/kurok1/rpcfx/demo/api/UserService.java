package indi.kurok1.rpcfx.demo.api;

import indi.kurok1.rpcfx.client.RemoteService;

@RemoteService(serviceName = "indi.kurok1.rpcfx.demo.api.UserService", url = "http://localhost:8080/")
public interface UserService {

    User findById(int id);

}
