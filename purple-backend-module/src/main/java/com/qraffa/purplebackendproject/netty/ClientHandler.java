package com.qraffa.purplebackendproject.netty;

import com.qraffa.purplebackendproject.util.rpc.RpcRequest;
import com.qraffa.purplebackendproject.util.rpc.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端处理器
 * @author qraffa
 */
public class ClientHandler extends ChannelDuplexHandler {

    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse response = (RpcResponse)msg;
            logger.info(String.format("receive from server ===> %s", response.getRequestId()));
            DefaultFuture future = FutureResponseUtil.getFutureMap().get(response.getRequestId());
            future.setResponse(response);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest)msg;
            logger.info(String.format("send to server ===> %s", request.getRequestId()));
            FutureResponseUtil.getFutureMap().putIfAbsent(request.getRequestId(), new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(String.format("ctx close cause ==> %s", cause));
        ctx.close();
    }

}
