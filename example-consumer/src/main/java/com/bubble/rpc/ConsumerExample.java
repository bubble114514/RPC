package com.bubble.rpc;

import com.bubble.rpc.bootstrap.ConsumerBootstrap;
import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.config.RpcConfig;
import com.bubble.rpc.proxy.ServiceProxyFactory;
import com.bubble.rpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("泡泡");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}

