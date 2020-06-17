package com.qraffa.purple.util.judge;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * 进程信息
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRun {

    private List<String> cmd;

    private Process process;

    private JudgeResult result;

    public ProcessRun(List<String> cmd) {
        this.cmd = cmd;
    }

    public void start() throws IOException {
        process = new ProcessBuilder().command(cmd).start();
    }

    public JudgeResult waitResult() throws IOException {
        byte[] bytes = new byte[1024];
        process.getInputStream().read(bytes);
        result = JSON.parseObject(bytes, JudgeResult.class);
        return result;
    }
}
