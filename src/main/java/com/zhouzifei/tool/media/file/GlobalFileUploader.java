package com.zhouzifei.tool.media.file;


import com.zhouzifei.tool.consts.FileOSSConfig;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.media.file.service.BaseFileUploader;
import com.zhouzifei.tool.media.file.service.FileUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;


/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Service
public class GlobalFileUploader extends BaseFileUploader implements FileUploader {

    @Autowired
    FileOSSConfig ossConfig;

    @Override
    public VirtualFile upload(InputStream file, String uploadType, String suffix, String storageType) {
        ApiClient apiClient = this.getApiClient(uploadType,storageType,ossConfig);
        return apiClient.uploadImg(file, suffix);
    }

    @Override
    public VirtualFile upload(File file, String uploadType,String storageType) {
        ApiClient apiClient = this.getApiClient(uploadType,storageType,ossConfig);
        return apiClient.uploadImg(file);
    }

    @Override
    public VirtualFile upload(MultipartFile file, String uploadType,String storageType) {
        ApiClient apiClient = this.getApiClient(uploadType,storageType,ossConfig);
        return apiClient.uploadImg(file);
    }

    @Override
    public boolean delete(String filePath, String uploadType) {
        return false;
    }
}
