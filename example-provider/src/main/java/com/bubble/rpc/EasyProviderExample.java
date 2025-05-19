package com.bubble.rpc;



import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.provider.service.UserServiceImpl;
import com.bubble.rpc.registry.LocalRegistry;
import com.bubble.rpc.server.HttpServer;
import com.bubble.rpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}