package com.qraffa.purplebackendproject.netty;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 保存Netty channel
 * @author qraffa
 */
public class ChannelHolder {
    public final static AtomicReference<Channel> CHANNEL_REFERENCE = new AtomicReference<>();
}
