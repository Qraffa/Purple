package com.qraffa.purplebackendproject.util;

import com.qraffa.purplebackendproject.bean.StatusInfo;
import com.qraffa.purplebackendproject.util.judge.JudgeResult;
import com.qraffa.purplebackendproject.util.judge.RunResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 判题结果写入redis
 * @author qraffa
 */
@Component
public class Result2Redis {

    @Autowired
    private RedisTemplate<String, String> template;

    private static RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate = template;
    }

    private final static String STATUS_KEY = "statusKey";

    private final static String[] RESULT_STRING = {
            "Wrong Answer", "Accept", "Time Limit Exceeded", "Time Limit Exceeded",
            "Memory Limit Exceeded", "Runtime Error", "System Error", "Compile Error"};

    /**
     * 判题结果写入redis
     * @param uid
     * @param resultList
     */
    public static void parseResult(String uid, List<RunResult> resultList) {
        int testCase = 0;
        for (RunResult run : resultList) {
            StringBuilder sb = new StringBuilder(uid);
            sb.append(".").append(++testCase);
            redisTemplate.opsForList().rightPush(STATUS_KEY, sb.toString());
            redisTemplate.opsForValue().set(sb.toString(), resultToString(run));
        }
    }

    /**
     * 返回码转换为字符串
     * @param run
     * @return
     */
    public static String resultToString(RunResult run) {
        if ("COMPILE".equals(run.getTask())) {
            return RESULT_STRING[7];
        }
        JudgeResult judgeResult = run.getJudgeResult();
        if ("0".equals(judgeResult.getExit_code()) && "0".equals(judgeResult.getError())) {
            return RESULT_STRING[Integer.parseInt(judgeResult.getResult()) + 1];
        }
        return RESULT_STRING[6];
    }

    /**
     * 返回状态列表
     * @return
     */
    public static List<StatusInfo> getStatusList() {
        List<String> keys = redisTemplate.opsForList().range(STATUS_KEY, 0, -1);
        List<StatusInfo> list = new ArrayList<>(keys.size());
        for(String key : keys) {
            StatusInfo info = new StatusInfo(key, redisTemplate.opsForValue().get(key));
            list.add(info);
        }
        return list;
    }
    
}
