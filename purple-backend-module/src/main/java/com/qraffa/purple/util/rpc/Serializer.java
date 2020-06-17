package com.qraffa.purple.util.rpc;

/**
 * 序列化接口
 * @author qraffa
 */
public interface Serializer {

    /**
     * 对象转换字节数组
     * @param object
     * @return
     * @throws Exception
     */
    byte[] serializer(Object object) throws Exception;

    /**
     * 字节数组转换对象
     * @param object
     * @param bytes
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T deserializer(Class<T> object, byte[] bytes) throws Exception;
}
