package com.qraffa.pruplejudgermodule.netty;

import com.qraffa.pruplejudgermodule.service.impl.HelloServiceImpl;
import com.qraffa.pruplejudgermodule.service.impl.JudgeServiceImpl;
import com.qraffa.pruplejudgermodule.util.rpc.RpcRequest;
import com.qraffa.pruplejudgermodule.util.rpc.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * 服务端处理类
 * @author qraffa
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {

    /**
     * springboot上下文用于获取bean
     */
    private ApplicationContext applicationContext;

    Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(msg.getRequestId());
        try {
            Object handler = handler(msg);
            response.setData(handler);
        } catch (Throwable throwable) {
            logger.info(throwable.toString());
            response.setErrorMsg(throwable.toString());
            throwable.printStackTrace();
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(String.format("ctx close cause ===> %s", cause));
        ctx.close();
    }

    public Object handler(RpcRequest request) throws Throwable {

        logger.info(String.format("接受到请求 ==> %s", request));
        Object bean = applicationContext.getBean(request.getClassName());
        Class<?> objClass = bean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] parameters = request.getParameters();
        FastClass fastClass = FastClass.create(objClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, paramTypes);
        return fastMethod.invoke(bean, parameters);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
