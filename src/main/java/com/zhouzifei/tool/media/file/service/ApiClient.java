package com.zhouzifei.tool.media.file.service;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.listener.ProgressListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public interface ApiClient {

    VirtualFile uploadFile(MultipartFile file);

    VirtualFile uploadFile(File file);

    VirtualFile uploadFile(InputStream is, String fileName);

    boolean removeFile(String key);

    void downloadFile(String key,  String localFile);

    VirtualFile saveToCloudStorage(String imgUrl, String referer,String key);

    VirtualFile multipartUpload(File file);
}
