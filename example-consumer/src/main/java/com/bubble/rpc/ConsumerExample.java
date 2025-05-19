package com.bubble.rpc;

import com.bubble.rpc.config.RpcConfig;
import com.bubble.rpc.utils.ConfigUtils;

/**
 * 建议服务消费者示例
 */
public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
