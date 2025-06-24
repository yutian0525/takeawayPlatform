package com.soft.controller;

import com.soft.common.BaseController;
import com.soft.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 *  文件上传控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@RestController
@CrossOrigin
public class FileController extends BaseController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url}")
    private String uploadUrl;

    /**
     * 文件上传接口
     * @param file 上传的文件
     * @return 文件访问URL
     */
    @PostMapping("/api/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("上传失败，请选择文件");
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成新文件名
        String newFileName = UUID.randomUUID().toString() + suffixName;
        
        // 创建文件存储目录
        File dest = new File(uploadPath);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        
        // 创建目标文件
        File targetFile = new File(uploadPath + newFileName);
        
        try {
            // 保存文件
            file.transferTo(targetFile);
            // 返回文件访问URL
            String fileUrl = uploadUrl + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }
}