package com.bubble.rpc.loadbalancer;

import com.bubble.rpc.spi.SpiLoader;

/**
 * 负载均衡工厂（工厂模式，用于获取负载均衡器对象）
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }
    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
