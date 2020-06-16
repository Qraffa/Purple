package com.qraffa.pruplejudgermodule.util.rpc;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author qraffa
 */
public class JsonSerializer implements Serializer {

    @Override
    public byte[] serializer(Object object) throws Exception {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserializer(Class<T> object, byte[] bytes) throws Exception {
        return JSON.parseObject(bytes, object);
    }

}
