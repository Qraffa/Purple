package com.qraffa.purple.util.judge;

import com.alibaba.fastjson.JSON;
import com.google.common.io.MoreFiles;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理判题任务
 * @author qraffa
 */
@Component
public class HandlerJudge {

    Logger logger = LoggerFactory.getLogger(HandlerJudge.class);

    @Autowired
    LanguageUtil languageUtil;

    @Value("${judger.judgePath}")
    private String judgePath;

    @Value("${judger.dataPath}")
    private String dataPath;

    @Value("${judger.owner}")
    private String ownerName;

    @Value("${judger.group}")
    private String groupName;

    @Value("${judger.clear}")
    private boolean clearJudge;

    private String COMPILE_TASK = "COMPILE";

    private final String RUN_TASK = "RUN";

    /**
     * 提交判题任务
     * @param requestId 判题id
     * @param code 源代码
     * @param lang 语言
     * @param problemId 题目id
     * @param time 时间限制
     * @param memory 内存限制
     * @return
     * @throws IOException
     */
    public List<RunResult> run(String requestId, String code, String lang, String problemId, String time, String memory) throws IOException {
        List<RunResult> results;
        // 创建文件
        createNewFile(requestId, code, lang);
        // 编译
        String compilerResult = compiler(requestId, lang);
        if (compilerResult.startsWith(judgePath)) {
            // 编译成功,返回执行结果
            results = judge(requestId, problemId, compilerResult, time, memory);
            // 运行完成,比较结果
            results = compare(requestId, problemId, results);
        } else {
            // 编译失败,返回运行结果
            results = new ArrayList<>(2);
            results.add(JSON.parseObject(compilerResult, RunResult.class));
        }
        // 默认配置清除判题文件。
        if (clearJudge) {
            // 删除文件
            delJudgeFile(requestId);
        }

        return results;
    }

    /**
     * 创建判题文件夹
     * @param requestId
     * @param code
     * @param lang
     * @throws IOException
     */
    public void createNewFile(String requestId, String code, String lang) throws IOException {
        // 创建新文件夹
        String requestPath = fullJudgePath(requestId);
        Path path = Paths.get(requestPath);
        // 判断文件是否存在
        if (!Files.exists(path)) {
            // 创建文件夹
            Files.createDirectory(path);
        }
        // 修改文件夹的所有者和组
        chown(path, ownerName, groupName);
        // 创建源代码文件
        Path source = Paths.get(String.format("%s/main.%s", requestPath, lang));
        try (FileOutputStream fos = new FileOutputStream(source.toFile())) {
            fos.write(code.getBytes());
            fos.flush();
        }
    }

    /**
     * 删除判题文件夹
     * @param requestId
     * @throws IOException
     */
    public void delJudgeFile(String requestId) throws IOException {
        // 获取文件夹
        String requestPath = fullJudgePath(requestId);
        Path path = Paths.get(requestPath);
        // 删除文件
        if (Files.exists(path)) {
            // guava工具类删除文件夹
            MoreFiles.deleteRecursively(path);
        }
    }

    /**
     * 修改文件夹的所有者和组
     * @param path
     * @param owner
     * @param group
     * @throws IOException
     */
    private void chown(Path path, String owner, String group) throws IOException {
        // 查找服务可用于查找用户或组名称
        UserPrincipalLookupService service = FileSystems.getDefault()
                .getUserPrincipalLookupService();
        // 根据名称来查找用户
        UserPrincipal userPrincipal = service.lookupPrincipalByName(owner);
        // 根据名称来查找组
        GroupPrincipal groupPrincipal = service.lookupPrincipalByGroupName(group);
        // 获取文件的属性
        PosixFileAttributeView fileAttributeView =
                Files.getFileAttributeView(path, PosixFileAttributeView.class);
        // 设置文件的所有者和组
        fileAttributeView.setOwner(userPrincipal);
        fileAttributeView.setGroup(groupPrincipal);
    }

    /**
     * 编译源代码
     * @param requestId
     * @param lang
     * @return 如果编译成功通过,返回执行文件路径,否则返回运行结果result
     * @throws IOException
     */
    public String compiler(String requestId, String lang) throws IOException {
        String srcPath = String.format("%s/main.%s", fullJudgePath(requestId), lang);
        String exePath = String.format("%s/main", fullJudgePath(requestId), lang);
        // 编译运行
        List<String> compiler = languageUtil.CompilerC(srcPath, exePath);
        // 创建进程
        ProcessBuilder pb = new ProcessBuilder();
        // 设置命令参数
        pb.command(compiler);
        // 合并标准输出流和错误流
        pb.redirectErrorStream(true);
        // 运行
        Process process = pb.start();
        // 获取结果
        byte[] bytes = new byte[2048];
        process.getInputStream().read(bytes);
        JudgeResult result = JSON.parseObject(bytes, JudgeResult.class);
        // 判断编译是否通过
        if ("0".equals(result.getExit_code()) && "0".equals(result.getResult())
            && "0".equals(result.getError())) {
            // 编译通过
            return exePath;
        } else {
            // 编译失败
            RunResult runResult = new RunResult(COMPILE_TASK, result, null, null);
            return JSON.toJSONString(runResult);
        }
    }

    /**
     * 执行判题任务
     * @param requestId
     * @param problemId
     * @param exePath
     * @param time
     * @param memory
     * @return 正常执行,返回结果集合,否则返回null
     * @throws IOException
     */
    public List<RunResult> judge(String requestId, String problemId, String exePath, String time, String memory) throws IOException {
        List<JudgeTestCase> testCases = getTestCases(problemId);
        if (testCases != null) {
            // 测试数据集合
            int caseNumber = testCases.size();
            // 任务执行集合
            List<ProcessRun> runList = new ArrayList<>(caseNumber);
            // 任务结果集合
            List<RunResult> resultList = new ArrayList<>(caseNumber);
            // 创建多个任务
            for (int i = 0; i < caseNumber; i++) {
                List<String> cmd = languageUtil.RunC(exePath, time, memory,
                        String.format("%s/%s", fullDataPath(problemId), testCases.get(i).getInputName()),
                        String.format("%s/%s", fullJudgePath(requestId), testCases.get(i).getOutputName()));
                runList.add(new ProcessRun(cmd));
                // 设置运行的输入数据
                resultList.add(new RunResult(RUN_TASK, null, testCases.get(i).getInputName(), null));
            }
            // 全部执行
            for (int i = 0; i < caseNumber; i++) {
                runList.get(i).start();
            }
            // 阻塞获取结果
            for (int i = 0; i < caseNumber; i++) {
                // 设置结果
                resultList.get(i).setJudgeResult(runList.get(i).waitResult());
            }
            return resultList;
        }
        return null;
    }

    /**
     * 输入输出比较
     * @param requestId
     * @param problemId
     * @param results
     */
    private List<RunResult> compare(String requestId, String problemId, List<RunResult> results) throws IOException {
        // 获取测试数据集合
        List<JudgeTestCase> testCases = getTestCases(problemId);
        int caseLen = testCases.size();
        // 输入文件和标准输出的md5映射关系
        Map<String, String> inputAndMd5 = new HashMap<>(caseLen);
        if (testCases != null) {
            for (int i = 0; i < caseLen; i++) {
                inputAndMd5.put(testCases.get(i).getInputName(), testCases.get(i).getOutputMd5());
            }
        }
        // 处理输出结果
        int resultLen = results.size();
        for (int i = 0; i < resultLen; i++) {
            RunResult runResult = results.get(i);
            JudgeResult judgeResult = runResult.getJudgeResult();
            // 成功运行,才计算结果文件的md5
            if ("0".equals(judgeResult.getExit_code()) && "0".equals(judgeResult.getResult())
                    && "0".equals(judgeResult.getError())) {
                // 获取输出结果文件
                String inputFile = runResult.getInputFile();
                String outFile = inputFile.substring(0, inputFile.length() - 3) + ".out";
                Path path = Paths.get(fullJudgePath(requestId), outFile);
                // 计算输出结果文件的md5
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    runResult.setOutputMd5(DigestUtils.md5Hex(fis));
                }
                // 比较标准输出和结果输出的md5,如果不相同,则将result置为-1.表示答案错误
                if (!runResult.getOutputMd5().equals(inputAndMd5.get(runResult.getInputFile()))) {
                    runResult.getJudgeResult().setResult("-1");
                }
            }
        }
        return results;
    }

    /**
     * 获取测试数据info文件
     * @param problemId
     * @return
     * @throws IOException
     */
    private List<JudgeTestCase> getTestCases(String problemId) throws IOException {
        // 读取判题信息文件
        Path path = Paths.get(String.format("%s/info", fullDataPath(problemId)));
        if (Files.exists(path)) {
            JudgeInfo info = null;
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                byte[] bytes = new byte[1024];
                fis.read(bytes);
                info = JSON.parseObject(bytes, JudgeInfo.class);
            }
            return info.getTestCases();
        } else {
            logger.warn("题目:{} 测试数据info文件不存在", problemId);
            return null;
        }
    }

    /**
     * 返回判题文件夹路径
     * @param requestId
     * @return
     */
    private String fullJudgePath(String requestId) {
        return String.format("%s/%s", judgePath, requestId);
    }

    /**
     * 返回测试数据文件夹路径
     * @param problemId
     * @return
     */
    private String fullDataPath(String problemId) {
        return String.format("%s/%s", dataPath, problemId);
    }

}
