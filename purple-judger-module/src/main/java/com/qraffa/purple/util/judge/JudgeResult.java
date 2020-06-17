package com.qraffa.purple.util.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 判题运行结果
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResult {

    private String cpu_time;
    private String real_time;
    private String memory;
    private String signal;
    private String exit_code;
    private String result;
    private String error;

}

/**
 * cpu_time: cpu time the process has used
 * real_time: actual running time of the process
 * memory: max vaule of memory used by the process
 * signal: signal number
 * exit_code: process's exit code
 * result: judger result, details in runner.h
 * error: args validation error or judger internal error, error code in runner.h
 */
