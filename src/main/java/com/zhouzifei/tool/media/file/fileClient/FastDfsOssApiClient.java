package com.zhouzifei.tool.media.file.fileClient;


import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.FileUtil;
import com.zhouzifei.tool.media.file.service.FastdfsClientUtil;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
@Component
public class FastDfsOssApiClient extends BaseApiClient {

    private String serverUrl;
    private String domainUrl;

    @Autowired
    FastdfsClientUtil fastdfsClientUtil;

    @PostConstruct
    public void init(){
        fastdfsClientUtil = this.fastdfsClientUtil;
    }
    public FastDfsOssApiClient() {
        super("FastDFS");
    }

    public FastDfsOssApiClient init(String serverUrl, String domainUrl) {
        this.serverUrl = serverUrl;
        this.domainUrl = domainUrl;
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key);
        final String s = fastdfsClientUtil.uploadFile(is, key);
        return new VirtualFile()
                .setOriginalFileName(key)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFullFilePath(this.serverUrl + s);
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        // 删除文件
        final FastdfsClientUtil fastdfsClientUtil = new FastdfsClientUtil();
        fastdfsClientUtil.deleteFile(key);
        return true;
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        Date startTime = new Date();
        final String fileName = file.getName();
        this.createNewFileName(fileName);
        final String s = fastdfsClientUtil.uploadFile(file);
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFullFilePath(this.serverUrl +s);
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, String fileName) {
        Date startTime = new Date();
        this.createNewFileName(fileName);
        final String s = fastdfsClientUtil.uploadFile(inputStream,fileName);
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFullFilePath(this.serverUrl +s);
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        final byte[] bytes = fastdfsClientUtil.downFile(key);
        return new ByteArrayInputStream(bytes);
    }
}
