package com.zhouzifei.tool.media.file.FileClient;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.GlobalFileException;
import com.zhouzifei.tool.exception.OssApiException;
import com.zhouzifei.tool.exception.QiniuApiException;
import com.zhouzifei.tool.html.Randoms;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.ImageUtil;
import com.zhouzifei.tool.media.file.service.ApiClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
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
    public VirtualFile uploadFile(MultipartFile file) {
        if (file == null) {
            throw new OssApiException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            VirtualFile res = this.uploadFile(file.getInputStream(), file.getOriginalFilename());
            VirtualFile imageInfo = ImageUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getOriginalFilename());
        } catch (IOException e) {
            throw new GlobalFileException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(File file) {
        if (file == null) {
            throw new QiniuApiException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            VirtualFile res = this.uploadFile(is, "temp" + FileUtil.getSuffix(file));
            VirtualFile imageInfo = ImageUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getName());
        } catch (IOException e) {
            throw new GlobalFileException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    void createNewFileName(String key, String pathPrefix) {
        this.suffix = FileUtil.getSuffix(key);
        String fileName = Randoms.alpha(16);
        this.newFileName = pathPrefix + (fileName + this.suffix);
    }

    /**
     * 将网络图片转存到云存储中
     *
     * @param imgUrl  网络图片地址
     * @param referer 为了预防某些网站做了权限验证，不加referer可能会403
     */
    @Override
    public VirtualFile saveToCloudStorage(String imgUrl, String referer) {
        try (InputStream is = FileUtil.getInputStreamByUrl(imgUrl, referer)) {
            return this.uploadFile(is, imgUrl);
        } catch (Exception e) {
            throw new GlobalFileException(e.getMessage());
        }
    }

    //protected abstract void check();

    @Override
    public void downloadFile(String key, String localFile) {
        InputStream content = this.downloadFileStream(key);
        String saveFile = localFile + key;
        FileUtil.mkdirs(saveFile);
        FileUtil.down(content,saveFile);
    }

    public abstract InputStream downloadFileStream(String key);
}
