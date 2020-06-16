package com.qraffa.pruplejudgermodule.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Netty服务端
 * @author qraffa
 */
@Component
public class NettyServer {

    Logger logger = LoggerFactory.getLogger(NettyServer.class);

    /**
     * boss用于处理连接请求
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    /**
     * worker用于处理数据请求
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${netty.port}")
    private Integer port;

    @Autowired
    private ServerInitializer serverInitializer;

    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .localAddress(new InetSocketAddress(port))
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(serverInitializer);
        serverBootstrap.bind().addListener(future -> {
            if (future.isSuccess()) {
                logger.info("server is ready");
            } else {
                logger.error("server start fail");
            }
        });
    }

    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
