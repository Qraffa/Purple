package com.qraffa.purplebackendproject.netty;

import com.qraffa.purplebackendproject.util.rpc.RpcRequest;
import com.qraffa.purplebackendproject.util.rpc.RpcResponse;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author qraffa
 */
@Component
public class RpcInvoker implements InvocationHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getSimpleName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParameters(args);

        Channel channel = ChannelHolder.CHANNEL_REFERENCE.get();
        if (channel.isActive()) {
            channel.writeAndFlush(request).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.warn(String.format("发送失败,cause ===> %s", future.cause()));
                }
            }).await();
        }
        RpcResponse response = FutureResponseUtil.getResponse(request.getRequestId());
        return response.getData();
    }
}
