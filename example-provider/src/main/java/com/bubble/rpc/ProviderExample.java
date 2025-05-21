package com.bubble.rpc;

import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.config.RpcConfig;
import com.bubble.rpc.constant.RpcConstant;
import com.bubble.rpc.model.ServiceMetaInfo;
import com.bubble.rpc.provider.service.UserServiceImpl;
import com.bubble.rpc.registry.LocalRegistry;
import com.bubble.rpc.registry.Registry;
import com.bubble.rpc.registry.RegistryFactory;
import com.bubble.rpc.server.tcp.VertxTcpServer;

public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try {
            registry.register(serviceMetaInfo);
            System.out.println("服务注册成功，key=" + serviceMetaInfo.getServiceKey());
        } catch (Exception e) {
            System.err.println("服务注册失败:");
            e.printStackTrace();
            throw new RuntimeException(e); // 直接终止，避免无效启动
        }

        // 启动 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
