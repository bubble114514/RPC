package com.bubble.rpc.bootstrap;

import com.bubble.rpc.RpcApplication;
import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.config.RpcConfig;
import com.bubble.rpc.constant.RpcConstant;
import com.bubble.rpc.model.ServiceMetaInfo;
import com.bubble.rpc.model.ServiceRegisterInfo;
import com.bubble.rpc.registry.LocalRegistry;
import com.bubble.rpc.registry.Registry;
import com.bubble.rpc.registry.RegistryFactory;
import com.bubble.rpc.server.tcp.VertxTcpServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务注册信息类
 */
@Data
@AllArgsConstructor

public class ProviderBootstrap {
    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?> > serviceRegisterInfoList) {
        // RPC 框架初始化
        RpcApplication.init();
        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();


        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList){
            String serviceName=serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
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
                throw new RuntimeException(serviceName+"服务注册失败:"+e);
            }
        }

        // 启动 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
