package com.bubble.rpc.registry;

import com.bubble.rpc.spi.SpiLoader;

/**
 * 注册中心工厂（获取注册中心对象）
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }
    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY=new EtcdRegistry();

    /**
     * 获取实例
     */
    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class,key);
    }
}
