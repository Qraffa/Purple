package com.qraffa.purple.service;

import com.qraffa.purple.bean.JudgeInfo;

/**
 * 提交判题任务接口
 * @author qraffa
 */
public interface RunService {

    /**
     * 提交判题任务
     * @param info 判题信息
     */
    void judge(JudgeInfo info);

    /**
     * 测试dubbo rpc
     * @param name
     * @return
     */
    String hello(String name);

}
