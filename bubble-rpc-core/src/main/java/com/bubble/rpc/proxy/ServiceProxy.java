package com.bubble.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.bubble.rpc.RpcApplication;
import com.bubble.rpc.config.RpcConfig;
import com.bubble.rpc.constant.ProtocolConstant;
import com.bubble.rpc.constant.RpcConstant;
import com.bubble.rpc.loadbalancer.LoadBalancer;
import com.bubble.rpc.loadbalancer.LoadBalancerFactory;
import com.bubble.rpc.model.RpcRequest;
import com.bubble.rpc.model.RpcResponse;
import com.bubble.rpc.model.ServiceMetaInfo;
import com.bubble.rpc.protocol.*;
import com.bubble.rpc.registry.Registry;
import com.bubble.rpc.registry.RegistryFactory;
import com.bubble.rpc.serializer.Serializer;
import com.bubble.rpc.serializer.SerializerFactory;
import com.bubble.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();

        //构建请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            //序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()){
                throw new RuntimeException("暂无服务地址");
            }
            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //将调用方法名（请求路径）作为负载均衡参数
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("methodName", method.getName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            //发送TCP请求
            RpcResponse rpcResponse = VertxTcpClient.doRequset(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();


        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }

    }
}
