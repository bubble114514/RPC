package com.bubble.rpc.server;

/**
 * 业务流程：
 *      1、反序列化请求为对象，并从请求对象中获取参数
 *      2、根据服务名称从本地注册器中获取到对应的服务实现类
 *      3、通过反射机制调用方法。得到返回结果
 *      4、对返回结果进行封装和序列化，并写入到响应中
 */

import com.bubble.rpc.model.RpcRequest;
import com.bubble.rpc.model.RpcResponse;
import com.bubble.rpc.registry.LocalRegistry;
import com.bubble.rpc.serializer.JdkSerializer;
import com.bubble.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP请求处理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer = new JdkSerializer();

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        //异步处理HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest =null;
            try {
                rpcRequest =serializer.desserialize(bytes, RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            //构造响应结果的对象
            RpcResponse rpcResponse=new RpcResponse();
            if (rpcRequest ==null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try {
                //获取要调用的服务实现类，通过反射调用
                Class<?> implClass= LocalRegistry.get(rpcRequest.getServiceName());
                Method method=implClass.getMethod(rpcRequest.getMethodName(),
                                                  rpcRequest.getParameterTypes());
                Object result=method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request,rpcResponse,serializer);

        });
    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            //序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        }catch (IOException e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }

}
