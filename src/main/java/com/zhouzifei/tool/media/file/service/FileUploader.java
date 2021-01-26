package com.zhouzifei.tool.media.file.service;

import com.zhouzifei.tool.entity.VirtualFile;
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
public interface FileUploader {

    /**
     * 上传文件
     *  @param file       待上传的文件流
     * @param uploadType 文件上传类型，用来区分文件
     * @param suffix     文件后缀
     */
    VirtualFile upload(InputStream file, String uploadType, String suffix,String storageType);

    /**
     * 上传文件
     *
     * @param file       待上传的文件
     * @param uploadType 文件上传类型，用来区分文件
     * @param storageType       配置
     */
    VirtualFile upload(File file, String uploadType,String storageType);

    /**
     * 上传文件
     *
     * @param file       待上传的文件
     * @param uploadType 文件上传类型，用来区分文件
     * @param storageType       配置
     */
    VirtualFile upload(MultipartFile file, String uploadType,String storageType);

    /**
     * 删除文件
     *
     * @param filePath   文件路径
     * @param uploadType 文件类型
     */
    boolean delete(String filePath, String uploadType);
}
