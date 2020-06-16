package com.qraffa.purplebackendproject.netty;

import com.qraffa.purplebackendproject.util.rpc.RpcRequest;
import com.qraffa.purplebackendproject.util.rpc.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Netty客户端
 * @author qraffa
 */
@Component
public class NettyClient {

    Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup group = new NioEventLoopGroup();

    @Value("${netty.host}")
    private String host;

    @Value("${netty.port}")
    private Integer port;

    private ChannelFuture channelFuture = null;

    private ClientInitializer initializer = null;

    private final int MAX_RETRY = 4;

    /**
     * 启动
     */
    @PostConstruct
    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        initializer = new ClientInitializer();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(initializer);
        try {
            connect0(bootstrap, host, port, MAX_RETRY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立连接
     * @param bootstrap
     * @param host
     * @param port
     */
    private void connect0(Bootstrap bootstrap, String host, int port, int retry) throws InterruptedException {
        channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("connect to server ok!");
            } else if (retry == 0) {
                logger.warn(String.format("已经重连[%d]次,连接放弃\n", MAX_RETRY));
            } else {
                // 第几次尝试
                int order = (MAX_RETRY - retry) + 1;
                // 设置本次重连间隔
                int delay = 1 << order;
                logger.warn(String.format("%s 第%d次重连\n", LocalDateTime.now(), order));
                // 失败重连,使用定时任务
                bootstrap.config().group().schedule(() -> {
                    try {
                        connect0(bootstrap, host, port, retry - 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, delay, TimeUnit.SECONDS);
            }
        }).await();
        // 保存channel实例
        ChannelHolder.CHANNEL_REFERENCE.set(channelFuture.channel());
    }

    /**
     * 检查连接是否建立
     * @return
     */
    public boolean isActive() {
        return channelFuture.channel().isActive();
    }

}
