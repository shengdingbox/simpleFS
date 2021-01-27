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
     * @param pathPrefix 图片前缀
     * @param imageUrl     网络地址
     */
    VirtualFile upload(InputStream file, String pathPrefix, String imageUrl);

    /**
     * 上传文件
     *
     * @param file       待上传的文件
     * @param pathPrefix 图片前缀
     */
    VirtualFile upload(File file, String pathPrefix);

    /**
     * 上传文件
     *
     * @param file       待上传的文件
     * @param pathPrefix 图片前缀
     */
    VirtualFile upload(MultipartFile file, String pathPrefix);

    /**
     * 删除文件
     *
     * @param filePath   文件路径
     * @param imageUrl 文件类型
     */
    boolean delete(String filePath, String imageUrl);

    String download(String imgUrl, String referer, String localPath);

    VirtualFile saveToCloudStorage(String imgUrl, String referer);
}
