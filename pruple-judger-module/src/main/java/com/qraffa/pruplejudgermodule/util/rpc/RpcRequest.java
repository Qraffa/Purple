package com.qraffa.pruplejudgermodule.util.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc请求类
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] parameters;
}
