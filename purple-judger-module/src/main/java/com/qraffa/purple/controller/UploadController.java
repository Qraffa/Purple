package com.qraffa.purple.controller;

import com.qraffa.purple.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 添加数据文件
 * @author qraffa
 */
@RestController
@RequestMapping("/")
public class UploadController {

    @Autowired
    UploadService uploadService;

    @PostMapping("upload")
    public String uploadZip(MultipartFile file, String id) {
        return uploadService.upload(file, id);
    }
}
