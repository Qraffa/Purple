package com.qraffa.purple.service.impl;

import com.google.common.io.MoreFiles;
import com.qraffa.purple.service.UploadService;
import com.qraffa.purple.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadServiceImpl implements UploadService {

    @Value("${judger.dataPath}")
    private String dataPath;

    /**
     * zip文件的魔数，用于检测文件类型
     */
    private static final String ZIP_HEADER = "504b0304";

    /**
     * 上传数据压缩文件，并解压到目录
     * @param file 数据文件
     * @param id 题目id,作为文件夹标识
     * @return
     */
    @Override
    public String upload(MultipartFile file, String id) {
        try {
            if (file.isEmpty()) {
                return "file is empty";
            }
            if (!checkFile(file)) {
                return "file is not zip";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = id + ".zip";
        Path path = Paths.get(dataPath, fileName);
        try {
            file.transferTo(path);
            FileUtil.unzip(path, dataPath+"/"+id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "upload ok";
    }

    /**
     * 通过文件头来检查文件类型
     * @param file
     * @return
     * @throws IOException
     */
    public boolean checkFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        int headerLength = 4;
        if (bytes.length > headerLength) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < headerLength; i++) {
                int v = bytes[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    sb.append(0);
                }
                sb.append(hv);
            }
            return ZIP_HEADER.equalsIgnoreCase(sb.toString());
        }
        return false;
    }
}
