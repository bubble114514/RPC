package com.bubble.rpc.config;

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
}
