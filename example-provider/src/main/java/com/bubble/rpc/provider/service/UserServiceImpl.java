package com.bubble.rpc.provider.service;

import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;

/**
 *
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名: "+user.getName());
        return user;
    }
}
