package com.qraffa.purple;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class PurpleBackendProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurpleBackendProjectApplication.class, args);
    }

}
