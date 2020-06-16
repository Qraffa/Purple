package com.qraffa.purplebackendproject.controller;

import com.alibaba.fastjson.JSON;
import com.qraffa.purplebackendproject.bean.JudgeInfo;
import com.qraffa.purplebackendproject.bean.StatusInfo;
import com.qraffa.purplebackendproject.netty.ProxyFactory;
import com.qraffa.purplebackendproject.service.JudgeService;
import com.qraffa.purplebackendproject.service.RunService;
import com.qraffa.purplebackendproject.util.Result2Redis;
import com.qraffa.purplebackendproject.util.judge.RunResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author qraffa
 */
@Controller
public class HelloController {

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    RunService runService;

    @RequestMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("hello", "purple judge");
        model.addAttribute("judgeInfo", new JudgeInfo());
        return "hello";
    }

    @PostMapping("/run")
    public String run(@ModelAttribute JudgeInfo judgeInfo) {
        String code = judgeInfo.getCode();
        // 提交任务
        runService.judge(code);
        return "redirect:status";
    }

    @RequestMapping("/status")
    public String status(Model model) {
        List<StatusInfo> infoList = Result2Redis.getStatusList();
        model.addAttribute("infoList", infoList);
        return "status";
    }
}
