package com.bubble.rpc;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.bubble.rpc.common.model.User;
import com.bubble.rpc.common.service.UserService;
import com.bubble.rpc.model.RpcRequest;
import com.bubble.rpc.model.RpcResponse;
import com.bubble.rpc.serializer.JdkSerializer;
import com.bubble.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();

        //发请求
        RpcRequest rpcRequest=RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse= HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
