package com.bubble.rpc.springbootprovider;

import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.springboot.start.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
