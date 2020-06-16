package com.qraffa.purplebackendproject.netty;

import com.qraffa.purplebackendproject.util.rpc.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * future获取结果
 * @author qraffa
 */
public class FutureResponseUtil {

    /**
     * 请求与响应的映射关系
     */
    private static final Map<String, DefaultFuture> FUTURE_MAP = new ConcurrentHashMap<>(64);

    public static Map<String, DefaultFuture> getFutureMap() {
        return FUTURE_MAP;
    }

    /**
     * 获取响应
     * @param requestId
     * @return
     */
    public static RpcResponse getResponse(String requestId) {
        try {
            DefaultFuture future = FUTURE_MAP.get(requestId);
            return future.getResponse(100);
        } finally {
            FUTURE_MAP.remove(requestId);
        }
    }

}
