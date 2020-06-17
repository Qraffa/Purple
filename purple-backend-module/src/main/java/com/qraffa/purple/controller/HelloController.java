package com.qraffa.purple.controller;

import com.qraffa.purple.bean.JudgeInfo;
import com.qraffa.purple.bean.StatusInfo;
import com.qraffa.purple.service.RunService;
import com.qraffa.purple.util.Result2Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qraffa
 */
@Controller
public class HelloController {

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    RunService runService;

    @GetMapping("/test")
    @ResponseBody
    public String test(String name) {
        return runService.hello(name);
    }

    @RequestMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("hello", "purple judge");
        String sampleCode = "    #include  &lt;stdio.h&gt;</br>" +
                "        &nbsp;&nbsp;int main() {</br>" +
                "        &nbsp;&nbsp;int a,b;</br>" +
                "        &nbsp;&nbsp;scanf(\"%d%d\",&a,&b);</br>" +
                "        &nbsp;&nbsp;printf(\"%d\\n\", a+b);</br>" +
                "        &nbsp;&nbsp;return 0;</br>" +
                "        }";
        model.addAttribute("sample", sampleCode);
        model.addAttribute("judgeInfo", new JudgeInfo());
        return "hello";
    }

    @PostMapping("/run")
    public String run(@ModelAttribute JudgeInfo judgeInfo) {
        logger.info("提交任务,题目id:{}", judgeInfo.getProblemId());
        // 提交任务
        if (judgeInfo.getProblemId() == null || judgeInfo.getProblemId().equals("")) {
            judgeInfo.setProblemId("1");
        }
        runService.judge(judgeInfo);
        return "redirect:status";
    }

    @RequestMapping("/status")
    public String status(Model model) {
        List<StatusInfo> infoList = Result2Redis.getStatusList();
        model.addAttribute("infoList", infoList);
        return "status";
    }
}
