package com.zhouzifei.tool.media.file.FileClient;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.ImageUtil;
import com.zhouzifei.tool.media.file.service.ApiClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
public abstract class BaseApiClient implements ApiClient {

    protected String storageType;
    protected String newFileName;
    protected String suffix;

    public BaseApiClient(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public VirtualFile uploadImg(MultipartFile file) {
        this.check();
        if (file == null) {
            throw new OssApiException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            VirtualFile res = this.uploadImg(file.getInputStream(), file.getOriginalFilename());
            VirtualFile imageInfo = ImageUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getOriginalFilename())
                    .setWidth(imageInfo.getWidth())
                    .setHeight(imageInfo.getHeight());
        } catch (IOException e) {
            throw new GlobalFileException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadImg(File file) {
        this.check();
        if (file == null) {
            throw new QiniuApiException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            VirtualFile res = this.uploadImg(is, "temp" + FileUtil.getSuffix(file));
            VirtualFile imageInfo = ImageUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getName())
                    .setWidth(imageInfo.getWidth())
                    .setHeight(imageInfo.getHeight());
        } catch (FileNotFoundException e) {
            throw new GlobalFileException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    void createNewFileName(String key, String pathPrefix) {
        this.suffix = FileUtil.getSuffix(key);
//        if (!FileUtil.isPicture(this.suffix)) {
//            throw new GlobalFileException("[" + this.storageType + "] 非法的图片文件[" + key + "]！目前只支持以下图片格式：[jpg, jpeg, png, gif, bmp]");
//        }
        String fileName = UUID.randomUUID().toString().replace("-","");
        this.newFileName = pathPrefix + (fileName + this.suffix);
    }

    protected abstract void check();
}
