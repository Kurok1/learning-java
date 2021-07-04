package indi.kurok1.rpcfx.demo.provider;

import indi.kurok1.rpcfx.demo.api.User;
import indi.kurok1.rpcfx.demo.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
