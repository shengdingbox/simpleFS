package com.zhouzifei.tool.media.file.fileClient;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.media.file.listener.ProgressListener;
import com.zhouzifei.tool.media.file.service.ApiClient;
import com.zhouzifei.tool.util.StringUtils;
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
    protected String folder = "";
    public ProgressListener progressListener = newListener();
    protected String suffix;
    protected String newFileName;

    protected  ProgressListener newListener(){
        return new ProgressListener() {
            @Override
            public void start(String msg) {}
            @Override
            public void process(int finished, int sum) { }
            @Override
            public void end(VirtualFile virtualFile) { }
        };
    }

    public BaseApiClient(String storageType) {
        this.storageType = storageType;
    }
    public ApiClient setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }
    @Override
    public VirtualFile uploadFile(MultipartFile file) {
        if (file == null) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try {
            VirtualFile res = this.uploadFile(file.getInputStream(), file.getOriginalFilename());
            VirtualFile imageInfo = FileUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getOriginalFilename());
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(File file) {
        if (file == null) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：文件不可为空");
        }
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            VirtualFile res = this.uploadFile(is, file.getName());
            VirtualFile imageInfo = FileUtil.getInfo(file);
            return res.setSize(imageInfo.getSize())
                    .setOriginalFileName(file.getName());
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }
    /**
     * 将网络图片转存到云存储中
     *
     * @param imgUrl  网络图片地址
     * @param referer 为了预防某些网站做了权限验证，不加referer可能会403
     */
    @Override
    public VirtualFile saveToCloudStorage(String imgUrl, String referer,String fileName) {
        try (InputStream is = FileUtil.getInputStreamByUrl(imgUrl, referer)) {
            if(StringUtils.isEmpty(fileName)){
                fileName = imgUrl;
            }
            return this.uploadFile(is, fileName);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    protected abstract void check();

    @Override
    public void downloadFile(String key, String localFile) {
        InputStream content = this.downloadFileStream(key);
        String saveFile = localFile + key;
        FileUtil.mkdirs(saveFile);
        FileUtil.down(content,saveFile);
    }
    void createNewFileName(String fileName) {
        this.suffix = FileUtil.getSuffix(fileName);
        this.newFileName = folder +fileName;
    }
    public abstract InputStream downloadFileStream(String key);


    @Override
    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
        return null;
    }
}
