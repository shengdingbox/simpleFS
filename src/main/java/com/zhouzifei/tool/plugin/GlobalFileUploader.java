package com.zhouzifei.tool.plugin;


import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.util.FileClient.ApiClient;
import com.zhouzifei.tool.util.FileClient.BaseFileUploader;
import com.zhouzifei.tool.util.FileClient.FileUploader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Map;


/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class GlobalFileUploader extends BaseFileUploader implements FileUploader {

    @Override
    public VirtualFile upload(InputStream file, String uploadType, String suffix, Map<String, Object> config) {
        ApiClient apiClient = this.getApiClient(uploadType,config);
        return apiClient.uploadImg(file, suffix);
    }

    @Override
    public VirtualFile upload(File file, String uploadType,Map<String, Object> config) {
        ApiClient apiClient = this.getApiClient(uploadType,config);
        return apiClient.uploadImg(file);
    }

    @Override
    public VirtualFile upload(MultipartFile file, String uploadType,Map<String, Object> config) {
        ApiClient apiClient = this.getApiClient(uploadType,config);
        return apiClient.uploadImg(file);
    }

    @Override
    public boolean delete(String filePath, String uploadType) {
        return false;
    }
}
