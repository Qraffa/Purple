package com.qraffa.purplebackendproject.controller;

import com.qraffa.purplebackendproject.netty.NettyClient;
import com.qraffa.purplebackendproject.netty.ProxyFactory;
import com.qraffa.purplebackendproject.service.HelloService;
import com.qraffa.purplebackendproject.service.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

/**
 * 测试处理层
 * @author qraffa
 */
@RestController
public class ConsumerController {

    @Autowired
    NettyClient nettyClient;

    @GetMapping("send")
    public String send() {
        HelloService service = ProxyFactory.create(HelloService.class);
        String ping = service.hello("ping");
        return ping;
    }

    @GetMapping("judge")
    public String judge() {
        JudgeService service = ProxyFactory.create(JudgeService.class);
        String code = "#include <stdio.h>\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "  int a,b;\n" +
                "  scanf(\"%d%d\",&a,&b);\n" +
                "  printf(\"%d\\n\",a+b);\n" +
                "  return 0;\n" +
                "}";
        String lang = "c";
        String problemId = "1";
        String time = "1000";
        String memory = String.valueOf(32 * 1024 * 1024);
        try {
            String judge = service.judge(UUID.randomUUID().toString(), code, lang, problemId, time, memory);
            return judge;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

}
