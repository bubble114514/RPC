package com.bubble.rpc;

import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.model.ServiceRegisterInfo;
import com.bubble.rpc.provider.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

//        // 服务提供者初始化
//        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
