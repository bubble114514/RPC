package com.bubble.rpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建 Vert.x实例
        Vertx vertx = Vertx.vertx();

        //创建HTTP服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        //监听端口并处理请求
        httpServer.requestHandler(new HttpServerHandler());

        //启动HTTP服务器并监听指定端口
        httpServer.listen(port,result->{
            if (result.succeeded()){
                System.out.println("HTTP server started on port "+port);
            }else {
                System.out.println("Failed to start HTTP server: "+result.cause());
            }
        });
    }
}
