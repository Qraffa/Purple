package com.qraffa.pruplejudgermodule.service.impl;

import com.qraffa.pruplejudgermodule.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author qraffa
 */
@Service("HelloService")
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String param) {
        System.out.println("hello service do");
        return String.format("pong ===> %s\n", param);
    }
}
