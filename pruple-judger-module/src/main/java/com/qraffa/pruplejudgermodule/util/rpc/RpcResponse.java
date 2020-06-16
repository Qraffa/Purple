package com.qraffa.pruplejudgermodule.util.rpc;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * rpc请求响应类
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {

    private String requestId;
    private String errorMsg;
    private Object data;

}
