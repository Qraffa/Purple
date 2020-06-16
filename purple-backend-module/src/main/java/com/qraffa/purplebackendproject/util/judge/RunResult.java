package com.qraffa.purplebackendproject.util.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运行结果,添加对应的测试样例
 * @author root
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunResult {

    private String task;

    private JudgeResult judgeResult;

    private String inputFile;

    private String outputMd5;

}
