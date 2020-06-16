package com.qraffa.pruplejudgermodule.netty;

import com.qraffa.pruplejudgermodule.util.rpc.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端初始化,添加处理器
 * @author qraffa
 */
@Component
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 线程池
     */
    private static final EventExecutorGroup GROUP = new DefaultEventExecutorGroup(8);

    @Autowired
    private ServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加请求解码器
        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonSerializer()));
        // 添加响应编码器
        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JsonSerializer()));
        // 添加自定义处理器
        pipeline.addLast(GROUP, serverHandler);
    }
}
