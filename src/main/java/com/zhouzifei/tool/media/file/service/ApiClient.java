package com.zhouzifei.tool.media.file.service;

import com.zhouzifei.tool.entity.VirtualFile;
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
    /**
     * MultipartFile类型的文件上传
     * @param file
     * @return 完成的文件信息
     */
    VirtualFile uploadFile(MultipartFile file);

    /**
     * File类型的文件上传
     * @param file
     * @return 完成的文件信息
     */
    VirtualFile uploadFile(File file);
    /**
     * InputStream类型的文件上传
     * @param is 文件流
     * @param fileName 文件名称
     * @return 完成的文件信息
     */
    VirtualFile uploadFile(InputStream is, String fileName);

    /**
     * 删除文件
     * @param fileName 文件名称
     * @return 是否删除
     */
    boolean removeFile(String fileName);

    /**
     * 下载文件
     * @param fileName 文件名称
     * @param localFile 本地路径
     */
    void downloadFile(String fileName,  String localFile);

    /**
     * 网络文件转存
     * @param fileUrl 文件地址
     * @param referer 鉴权信息
     * @param fileName 文件名称
     * @return
     */
    VirtualFile saveToCloudStorage(String fileUrl, String referer,String fileName);

    /**
     * MultipartFile类型的文件分片上传
     * @param file 文件
     * @return 完成的文件信息
     */
    VirtualFile multipartUpload(File file);
    /**
     * InputStream
     * @param inputStream 文件
     * @param fileName 文件名称
     * @return 完成的文件信息
     */
    VirtualFile multipartUpload(InputStream inputStream,String fileName);
    /**
     * InputStream
     * @param inputStream 文件
     * @param fileName 文件名称
     * @return 断点续传
     */
    abstract VirtualFile resumeUpload(InputStream inputStream,String fileName);


}
