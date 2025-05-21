package com.bubble.rpc.fault.tolerate;

import com.bubble.rpc.spi.SpiLoader;

/**
 * 容错策略工厂（工厂模式，用于获取容错策略对象）
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    public static final String DEFAULT_TOLERANT_STRATEGY="fail_fast";
    /**
     * 获取实例
     */
    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
