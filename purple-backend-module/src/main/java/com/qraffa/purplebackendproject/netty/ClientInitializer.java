package com.qraffa.purplebackendproject.netty;

import com.qraffa.purplebackendproject.util.rpc.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 初始化类,添加处理器
 * @author qraffa
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private ClientHandler clientHandler;

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加请求编码器
        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JsonSerializer()));
        // 添加响应解码器
        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonSerializer()));
        // 添加自定义处理器
        clientHandler = new ClientHandler();
        pipeline.addLast(clientHandler);
    }
}
