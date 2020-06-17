package com.qraffa.purple.service;

import java.io.IOException;

/**
 * @author qraffa
 */
public interface JudgeService {
    /**
     * 提交判题任务
     * @param requestId 判题id
     * @param code 源代码
     * @param lang 语言
     * @param problemId 题目id
     * @param time 时间限制
     * @param memory 内存限制
     * @return
     * @exception
     */
    String judge(String requestId, String code, String lang, String problemId, String time, String memory) throws IOException;
}
