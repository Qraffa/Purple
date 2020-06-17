package com.qraffa.purple.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.qraffa.purple.service.JudgeService;
import com.qraffa.purple.util.judge.HandlerJudge;
import com.qraffa.purple.util.judge.RunResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author root
 */
@Service(group = "judger", version = "1.0")
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    HandlerJudge handlerJudge;

    @Override
    public String judge(String requestId, String code, String lang, String problemId, String time, String memory) throws IOException {
        List<RunResult> run = handlerJudge.run(requestId, code, lang, problemId, time, memory);
        String jsonString = JSON.toJSONString(run);
        return jsonString;
    }
}
