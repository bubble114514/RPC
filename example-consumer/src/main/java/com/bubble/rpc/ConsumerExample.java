package com.bubble.rpc;

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
        //获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println("用户名："+newUser.getName());
        }else {
            System.out.println("用户不存在");
        }
        short number = userService.getNumber();
        System.out.println("number:"+number);

    }
}
