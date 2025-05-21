package com.bubble.rpc.fault.retry;

import com.bubble.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {
    /**
     * 重试
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
