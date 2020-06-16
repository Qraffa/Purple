package com.qraffa.pruplejudgermodule.util.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author qraffa
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private Serializer serializer;

    public RpcDecoder(Class<?> object, Serializer serializer) {
        this.clazz = object;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 判断能否读取长度
        if (in.readableBytes() < 4) {
            return;
        }
        // 标记readIndex
        // 必须要先标记再读,保证readIndex在首位,这样才能正确读到length
        in.markReaderIndex();
        // 获取数据长度
        int length = in.readInt();
        // 可读字节小于长度,消息不完整
        if (in.readableBytes() < length) {
            // 重置readIndex
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object o = serializer.deserializer(clazz, bytes);
        out.add(o);
    }
}
