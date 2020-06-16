package com.qraffa.purplebackendproject.netty;


import com.qraffa.purplebackendproject.util.rpc.RpcResponse;

/**
 * 封装请求和结果的映射
 * @author qraffa
 */
public class DefaultFuture {

    /**
     * 请求响应
     */
    private RpcResponse response;

    /**
     * 响应完成标志位
     */
    private volatile boolean isSuccess = false;

    private final Object lock = new Object();

    /**
     * 阻塞获取结果
     * @param timeout
     * @return
     */
    public RpcResponse getResponse(long timeout) {
        synchronized (lock) {
            while (!isSuccess) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    /**
     * 响应返回成功,设置值,唤醒其他线程
     * @param response
     */
    public void setResponse(RpcResponse response) {
        if (isSuccess) {
            return;
        }
        synchronized (lock) {
            isSuccess = true;
            this.response = response;
            lock.notifyAll();
        }
    }
}
