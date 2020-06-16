package com.qraffa.pruplejudgermodule.util.judge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 各语言编译参数
 * @author qraffa
 */
@Component
public class LanguageUtil {

    private static String max_cpu_time = String.valueOf(1000);
    private static String max_real_time = String.valueOf(2000);;
    private static String max_memory = String.valueOf(32 * 1024 * 1024);
    private static String max_stack = String.valueOf(32 * 1024 * 1024);
    private static String max_output_size = String.valueOf(1024 * 1024);
    private static String max_process_number = String.valueOf(-1);

    @Value("PATH=${path}")
    private String env;
    @Value("${judger.errorPath}")
    private String error_path;
    @Value("${judger.logPath}")
    private String log_path;
    @Value("${judger.uid}")
    private String uid;
    @Value("${judger.gid}")
    private String gid;

    private String default_input_path = "/dev/null";
    private String default_output_path = "/dev/null";

    @Value("${judger.judgeSO}")
    private String judge;

    /**
     * 编译C的参数
     * @return exec命令数组
     */
    public List<String> CompilerC(String src_path, String out_path) {
        String exe_path = "/usr/bin/gcc";
        String[] args = new String[]{
                "-DONLINE_JUDGE",
                //"-O2",
                "-w",
                "-fmax-errors=3",
                "-std=c11",
                src_path,
                "-lm",
                "-o",
                out_path
        };
        return defaultExec(exe_path, default_input_path, default_output_path, args);
    }

    /**
     * 运行C的参数
     * @return exec的命令数组
     */
    public List<String> RunC(String exec_path, String max_cpu_time, String max_memory,
                                String input_path, String out_path) {
        String my_max_real_time = String.valueOf(Integer.parseInt(max_cpu_time) + 5000);
        return exec(max_cpu_time, my_max_real_time, max_memory, "0", max_stack, max_process_number,
                max_output_size, exec_path, input_path, out_path, "c_cpp", env, error_path,
                log_path, uid, gid, null);
    }

    /**
     * judger执行参数
     * @param max_cpu_time
     * @param max_real_time
     * @param max_memory
     * @param memory_limit_check_only
     * @param max_stack
     * @param max_process_number
     * @param max_output_size
     * @param exe_path
     * @param input_path
     * @param output_path
     * @param seccomp_rule_name
     * @param env
     * @param error_path
     * @param log_path
     * @param uid
     * @param gid
     * @param args
     * @return
     */
    private List<String> exec(String max_cpu_time, String max_real_time, String max_memory,
                                 String memory_limit_check_only, String max_stack, String max_process_number,
                                 String max_output_size, String exe_path, String input_path,
                                 String output_path, String seccomp_rule_name, String env,
                                 String error_path, String log_path, String uid, String gid,
                                 String[] args) {
        List<String> exec = new ArrayList<>(40);
        String[] execFirst =  new String[]{
                judge,
                "--max_cpu_time=" + max_cpu_time,
                "--max_real_time=" + max_real_time,
                "--max_memory=" + max_memory,
                "--memory_limit_check_only=" + memory_limit_check_only,
                "--max_stack=" + max_stack,
                "--max_output_size=" + max_output_size,
                "--exe_path=" + exe_path,
                "--input_path=" + input_path,
                "--output_path=" + output_path,
                "--env=" + env,
                "--error_path=" + error_path,
                "--log_path=" + log_path,
                "--uid=" + uid,
                "--gid=" + gid
        };
        exec.addAll(Arrays.asList(execFirst));
        // 判断seccomp_rules是否限制
        if (seccomp_rule_name != null) {
            exec.add("--seccomp_rule_name=" + seccomp_rule_name);
        }
        // 添加参数
        if (args != null) {
            for (String arg : args) {
                exec.add("--args=" + arg);
            }
        }
        return exec;
    }

    /**
     * 设置默认执行参数
     * @param exe_path
     * @param input_path
     * @param output_path
     * @param args
     * @return
     */
    private List<String> defaultExec(String exe_path, String input_path, String output_path,
                                        String[] args) {
        return exec(max_cpu_time, max_real_time, max_memory, "0", max_stack, max_process_number,
                max_output_size, exe_path, input_path, output_path, null, env, error_path,
                log_path, uid, gid, args);
    }

}
