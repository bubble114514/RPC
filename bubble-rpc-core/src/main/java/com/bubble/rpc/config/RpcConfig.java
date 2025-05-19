package com.bubble.rpc.config;

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
}
