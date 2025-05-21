package com.bubble.rpc;

import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.model.ServiceMetaInfo;
import com.bubble.rpc.registry.EtcdRegistry;
import com.bubble.rpc.server.HttpServer;
import com.bubble.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) throws Exception {
        // RPC框架初始化
        RpcApplication.init();

        // 准备服务元信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceVersion("1.0");


        // 初始化并注册服务
        EtcdRegistry etcdRegistry = new EtcdRegistry();
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379"); // 确保与RpcApplication配置一致
        etcdRegistry.init(registryConfig); // 必须先初始化
        etcdRegistry.register(serviceMetaInfo);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}