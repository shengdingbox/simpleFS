package com.zhouzifei.tool.media.file;


import com.zhouzifei.tool.config.properties.FileOSSProperties;
import com.zhouzifei.tool.config.properties.SendSmsProperties;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.html.Randoms;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.media.file.service.BaseFileUploader;
import com.zhouzifei.tool.media.file.service.FileUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
@Service
@Slf4j
public class FileUploadUtil extends BaseFileUploader implements FileUploader {

    @Autowired
    FileOSSProperties fileOSSProperties;

    @Override
    public VirtualFile upload(InputStream file, String pathPrefix, String imageUrl) {
        ApiClient apiClient = this.getApiClient(pathPrefix, fileOSSProperties);
        return apiClient.uploadImg(file, imageUrl);
    }

    @Override
    public VirtualFile upload(File file, String pathPrefix) {
        ApiClient apiClient = this.getApiClient(pathPrefix, fileOSSProperties);
        return apiClient.uploadImg(file);
    }

    @Override
    public VirtualFile upload(MultipartFile file, String pathPrefix) {
        ApiClient apiClient = this.getApiClient(pathPrefix, fileOSSProperties);
        return apiClient.uploadImg(file);
    }

    @Override
    public boolean delete(String filePath, String imageUrl) {
        return false;
    }
    /**
     * 下载网络图片到本地<br>暂时不用，只供测试
     *
     * @param imgUrl    网络图片地址
     * @param referer   为了预防某些网站做了权限验证，不加referer可能会403
     * @param localPath 待保存的本地地址
     */

    @Override
    public  String download(String imgUrl, String referer, String localPath) {

        String fileName = localPath + File.separator + Randoms.alpha(16) + FileUtil.getSuffixByUrl(imgUrl);
        try (InputStream is = FileUtil.getInputStreamByUrl(imgUrl, referer);
             FileOutputStream fos = new FileOutputStream(fileName)) {
            if (null == is) {
                return null;
            }
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            int bytesWritten = 0, byteCount = 0;
            byte[] b = new byte[1024];
            while ((byteCount = is.read(b)) != -1) {
                fos.write(b, bytesWritten, byteCount);
            }
        } catch (IOException e) {
            log.error("Error.", e);
            return null;
        }
        return fileName;
    }
    /**
     * 将网络图片转存到云存储中
     *
     * @param imgUrl  网络图片地址
     * @param referer 为了预防某些网站做了权限验证，不加referer可能会403
     */
    @Override
    public  VirtualFile saveToCloudStorage(String imgUrl, String referer) {
        log.debug("download img >> {}", imgUrl);
        try (InputStream is = FileUtil.getInputStreamByUrl(imgUrl, referer)) {
            return upload(is, "/",imgUrl);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalFileException(e.getMessage());
        }
    }
}
