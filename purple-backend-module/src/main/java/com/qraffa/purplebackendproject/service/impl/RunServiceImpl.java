package com.qraffa.purplebackendproject.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.qraffa.purplebackendproject.netty.ProxyFactory;
import com.qraffa.purplebackendproject.service.JudgeService;
import com.qraffa.purplebackendproject.service.RunService;
import com.qraffa.purplebackendproject.util.Result2Redis;
import com.qraffa.purplebackendproject.util.judge.RunResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 提交判题任务实现类
 * @author qraffa
 */
@Service
public class RunServiceImpl implements RunService {

    Logger logger = LoggerFactory.getLogger(RunServiceImpl.class);

    /**
     * 使用guava创建线程池工厂
     */
    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("judge-pool-%d").build();

    /**
     * 创建任务提交线程池
     */
    private final static ExecutorService JUDGE_POOL = new ThreadPoolExecutor(4, 8, 0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(16), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void judge(String code) {
        // 预处理数据
        String lang = "c";
        String problemId = "1";
        String time = "1000";
        String memory = String.valueOf(32 * 1024 * 1024);
        String uid = UUID.randomUUID().toString().substring(0, 8);
        logger.info("judge id ===> {}", uid);
        JUDGE_POOL.execute(() -> {
            JudgeService service = ProxyFactory.create(JudgeService.class);
            try {
                String res = service.judge(uid, code, lang, problemId, time, memory);
                List<RunResult> results = JSON.parseArray(res, RunResult.class);
                // 结果写入redis
                Result2Redis.parseResult(uid, results);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
