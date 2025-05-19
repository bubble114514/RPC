package com.bubble.rpc.common.service;

import com.bubble.rpc.common.model.User;

public interface UserService {

    /**
     * 获取用户
     */
    User getUser(User user);

    /**
     * 新方法-获取数字
     */
    default short getNumber(){
        return 1;
    }
}
