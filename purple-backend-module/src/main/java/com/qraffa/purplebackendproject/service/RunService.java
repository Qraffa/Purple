package com.qraffa.purplebackendproject.service;

/**
 * 提交判题任务接口
 * @author qraffa
 */
public interface RunService {

    /**
     * 提交判题任务
     * @param code
     */
    void judge(String code);

}
