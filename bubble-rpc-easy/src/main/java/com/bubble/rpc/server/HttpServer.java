package com.bubble.rpc.server;

/**
 * Http服务接口
 */
public interface HttpServer {
    /**
     * 启动服务器
     */
    void doStart(int port);
}
