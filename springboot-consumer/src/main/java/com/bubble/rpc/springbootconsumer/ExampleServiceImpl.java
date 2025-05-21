package com.bubble.rpc.springbootconsumer;

import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.springboot.start.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("泡泡");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
