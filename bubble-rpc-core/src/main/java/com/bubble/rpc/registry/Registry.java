package com.bubble.rpc.registry;

import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.model.ServiceMetaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 注册中心
 */
public interface Registry {
    /**
     * 初始化
     */
    void init(RegistryConfig registryConfig);
    /**
     * 注册服务（服务端）
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    /**
     * 注销服务（服务端）
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception;
    /**
     * 服务发现（获取某服务的所有节点，消费端）
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
    /**
     * 服务销毁
     */
    void destroy();
    /**
     * 心跳检测（服务端）
     */
    void heartBeat();
    /**
     * 监听（消费端）
     */
    void watch(String key);

}
