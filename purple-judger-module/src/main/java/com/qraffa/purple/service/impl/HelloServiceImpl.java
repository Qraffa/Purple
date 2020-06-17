package com.qraffa.purple.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qraffa.purple.service.HelloService;

/**
 * @author qraffa
 */
@Service(group = "judger", version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String param) {
        return String.format("pong ===> %s\n", param);
    }
}
