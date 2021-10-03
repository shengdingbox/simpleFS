package com.zhouzifei.tool.media.file.fileClient;


import com.qcloud.cos.utils.IOUtils;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.common.fastdfs.ClientGlobal;
import com.zhouzifei.tool.media.file.common.fastdfs.StorageClient;
import com.zhouzifei.tool.media.file.common.fastdfs.TrackerClient;
import com.zhouzifei.tool.media.file.common.fastdfs.TrackerServer;
import com.zhouzifei.tool.media.file.common.fastdfs.common.NameValuePair;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * @author 周子斐
 * @date 2021/1/30
 * @Description
 */
@Component
@Slf4j
public class FastDfsOssApiClient extends BaseApiClient {

    private String serverUrl;
    private String domainUrl;

    public FastDfsOssApiClient() {
        super("FastDFS");
    }

    public FastDfsOssApiClient init(String serverUrl, String domainUrl) {
        Properties props = new Properties();
        props.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, serverUrl);
        try {
            ClientGlobal.initByProperties(props);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]尚未配置阿里云FastDfs，文件上传功能暂时不可用！");
        }
        this.serverUrl = serverUrl;
        this.domainUrl = domainUrl;
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        Date startTime = new Date();
        String key = FileUtil.generateTempFileName(imageUrl);
        this.createNewFileName(key);
        try {
            //tracker 客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取trackerServer
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            //创建StorageClient 对象
            StorageClient storageClient = new StorageClient(trackerServer);
            //文件元数据信息组
            NameValuePair[] nameValuePairs = {new NameValuePair("author", "huhy")};
            byte[] bytes = IOUtils.toByteArray(is);
            final String suffix = FileUtil.getSuffix(imageUrl);
            String[] txts = storageClient.upload_file(bytes, suffix, nameValuePairs);
            final String fullPath = String.join("/", txts);
            return new VirtualFile()
                    .setOriginalFileName(key)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFullFilePath(this.domainUrl + "/" + fullPath);
        } catch (IOException var6) {
            log.error("FDFS upload error ！ res：{}", var6);
            throw new ServiceException("文件上传异常!");
        }
    }

    @Override
    public boolean removeFile(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        //tracker 客户端
        TrackerClient trackerClient = new TrackerClient();
        //获取trackerServer
        try {
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            //创建StorageClient 对象
            StorageClient storageClient = new StorageClient(trackerServer);
            //文件元数据信息组
            final int deleteFile = storageClient.delete_file("group1", key);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        Date startTime = new Date();
        final String fileName = file.getName();
        this.createNewFileName(fileName);

        final String s = "FastdfsClientUtil.uploadFile(file);";
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFullFilePath(this.serverUrl + s);
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, String fileName) {
        Date startTime = new Date();
        this.createNewFileName(fileName);

        final String s = "FastdfsClientUtil.uploadFile(inputStream,fileName)";
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(this.newFileName)
                .setFullFilePath(this.serverUrl + s);
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        final byte[] bytes = "FastdfsClientUtil.downFile(key)".getBytes();
        return new ByteArrayInputStream(bytes);
    }
}
