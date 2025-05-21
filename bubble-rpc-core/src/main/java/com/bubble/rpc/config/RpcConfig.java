package com.bubble.rpc.config;

import com.bubble.rpc.fault.retry.RetryStrategyKeys;
import com.bubble.rpc.fault.tolerate.TolerantStrategyKeys;
import com.bubble.rpc.loadbalancer.LoadBalancerKeys;
import com.bubble.rpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name="bubble-rpc";
    /**
     * 版本号
     */
    private String version="1.0";
    /**
     * 服务主机名
     */
    private String serverHost="localhost";
    /**
     * 服务器端口号
     */
    private Integer serverPort =8080;
    /**
     * 模拟调用
     */
    private boolean mock=false;
    /**
     * 序列化器
     */
    private String serializer= SerializerKeys.JDK;
    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig=new RegistryConfig();
    /**
     * 负载均衡
     */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;
    /**
     * 重试策略
     */
    private String retryStrategy= RetryStrategyKeys.NO;
    /**
     * 容错策略
     */
    private String tolerantStrategy= TolerantStrategyKeys.FAIL_FAST;
}
