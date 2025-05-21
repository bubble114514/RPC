package com.bubble.rpc.springboot.start.annotation;

import com.bubble.rpc.springboot.start.bootstrap.RpcConsumerBootstrap;
import com.bubble.rpc.springboot.start.bootstrap.RpcInitBootstrap;
import com.bubble.rpc.springboot.start.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动 RPC 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动 server
     */
    boolean needServer() default true;
}
