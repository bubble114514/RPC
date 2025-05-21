package com.bubble.rpc.server.tcp;


import cn.hutool.core.util.IdUtil;
import com.bubble.rpc.RpcApplication;
import com.bubble.rpc.constant.ProtocolConstant;
import com.bubble.rpc.model.RpcRequest;
import com.bubble.rpc.model.RpcResponse;
import com.bubble.rpc.model.ServiceMetaInfo;
import com.bubble.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {

    /**
     * 发送请求
     */
    public static RpcResponse doRequset(RpcRequest rpcRequest, ServiceMetaInfo selectedServiceMetaInfo) throws ExecutionException, InterruptedException {
        //发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(),
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Connected to server : 服务端连接成功");
                        NetSocket socket = result.result();
                        //发送数据
                        //构造消息
                        ProtocolMessage<Object> protocolMessage = new ProtocolMessage<>();
                        ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);
                        //编码请求
                        try {
                            Buffer encodedBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                            socket.write(encodedBuffer);
                        }catch (IOException e){
                            throw new RuntimeException("协议消息编码错误");
                        }
                        //接收响应
                        socket.handler(buffer -> {
                            try {
                                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                responseFuture.complete(rpcResponseProtocolMessage.getBody());
                            } catch (IOException e) {
                                throw new RuntimeException("协议消息解码错误");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }else {
                        System.out.println("Failed to connect to server: " + result.cause());
                    }
                });
        RpcResponse rpcResponse = responseFuture.get();
        //关闭连接
        netClient.close();
        return rpcResponse;
    }

    public void start(){
        //创建Ver.x 实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888,"127.0.0.1",result->{
            if (result.succeeded()){
                System.out.println("Connected to server");
                NetSocket socket = result.result();
                //发送数据
                for(int i=0;i<1000;i++){
                    String str="Hello, server!Hello, server!Hello, server!Hello, server!";
                    Buffer buffer=Buffer.buffer();
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                //接收响应
                socket.handler(buffer->{
                    System.out.println("Received response: "+buffer.toString("UTF-8"));
                });
            }else {
                System.out.println("Failed to connect to server: "+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
