package com.bubble.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应类 RpcResponse：封装调用方法得到的返回值、已经调用的信息（比如异常情况）等
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 响应数据
     */
    private Object data;
    /**
     * 响应数据类型（预留）
     */
    private Class<?> dataType;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应异常信息
     */
    private Throwable exception;
}
