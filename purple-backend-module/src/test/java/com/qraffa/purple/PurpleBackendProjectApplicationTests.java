package com.qraffa.purple;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class PurpleBackendProjectApplicationTests {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${path}")
    String path;

    @Test
    void contextLoads() {
        System.out.println(path);
    }

}
