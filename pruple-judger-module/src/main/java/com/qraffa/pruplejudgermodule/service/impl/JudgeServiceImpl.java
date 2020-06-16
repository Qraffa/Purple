package com.qraffa.pruplejudgermodule.service.impl;

import com.alibaba.fastjson.JSON;
import com.qraffa.pruplejudgermodule.netty.HandlerJudge;
import com.qraffa.pruplejudgermodule.service.JudgeService;
import com.qraffa.pruplejudgermodule.util.judge.RunResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author root
 */
@Service("JudgeService")
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
