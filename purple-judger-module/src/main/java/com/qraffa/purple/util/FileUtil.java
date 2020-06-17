package com.qraffa.purple.util;

import com.google.common.io.MoreFiles;
import com.qraffa.purple.util.judge.JudgeInfo;
import com.qraffa.purple.util.judge.JudgeTestCase;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 文件工具类,解压测试数据文件
 * @author root
 */
public class FileUtil {

    /**
     * 解压缩文件
     * @param src 压缩文件路径
     * @param dest 解压路径
     * @throws Exception
     */
    public static void unzip(Path src, String dest) throws Exception {
        // 文件如果存在,则删除
        if (Files.exists(Paths.get(dest))) {
            MoreFiles.deleteRecursively(Paths.get(dest));
        }
        // 创建文件夹
        Files.createDirectory(Paths.get(dest));
        // 获取压缩文件
        ZipFile zipFile = new ZipFile(src.toFile());
        // 获取所有的文件
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        // 测试数据列表
        List<JudgeTestCase> testCases = new ArrayList<>(10);
        // 遍历文件
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            String name = entry.getName();
            // 设置解压缩路径
            Path unzipPath = Paths.get(dest, name);
            // 获取文件流
            try (InputStream in = zipFile.getInputStream(entry);
                 InputStream inMd5 = zipFile.getInputStream(entry);
                 FileOutputStream out = new FileOutputStream(unzipPath.toFile())) {
                // 获取.out文件的md5值
                if (name.endsWith(".out")) {
                    // 计算输入文件名称
                    String inName = name.substring(0, name.length() - 4) + ".in";
                    // 添加列表
                    testCases.add(new JudgeTestCase(inName, name, DigestUtils.md5Hex(inMd5)));
                }
                // 解压缩
                IOUtils.copy(in, out);
            }
        }
        // 写入info文件
        createInfoFile(testCases, dest);
    }

    /**
     * 测试数据写入info文件
     * @param testCases
     * @param path
     * @throws Exception
     */
    private static void createInfoFile(List<JudgeTestCase> testCases, String path) throws Exception {
        JudgeInfo info = new JudgeInfo(testCases);
        // info文件路径
        Path infoPath = Paths.get(path, "info");
        if (!Files.exists(infoPath)) {
            // 创建文件
            Files.createFile(infoPath);
        }
        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(infoPath.toFile())) {
            byte[] bytes = info.serializer(info);
            fos.write(bytes);
            fos.flush();
        }
    }

}
