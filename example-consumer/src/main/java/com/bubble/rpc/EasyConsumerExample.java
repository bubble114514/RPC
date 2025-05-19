package com.bubble.rpc;

import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.proxy.ServiceProxy;
import com.bubble.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
//        //静态代理
//        UserServiceProxy userServiceProxy = new UserServiceProxy();

        //动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);


        User user=new User();
        user.setName("张三");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println("用户名："+newUser.getName());
        }else {
            System.out.println("用户不存在");
        }
    }
}
