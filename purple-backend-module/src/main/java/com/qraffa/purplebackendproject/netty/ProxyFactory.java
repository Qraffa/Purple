package com.qraffa.purplebackendproject.netty;

import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @author qraffa
 */
@Component
public class ProxyFactory {

    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcInvoker());
    }

}
