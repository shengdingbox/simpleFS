//package com.zhouzifei.tool.media.file.fileClient;
//
//
//import com.zhouzifei.tool.entity.VirtualFile;
//import com.zhouzifei.tool.exception.OssApiException;
//import com.zhouzifei.tool.exception.ServiceException;
//import com.zhouzifei.tool.media.file.FileUtil;
//import com.zhouzifei.tool.media.file.service.FastdfsClientUtil;
//import com.zhouzifei.tool.media.file.service.UpaiManager;
//import com.zhouzifei.tool.util.StringUtils;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author 周子斐
// * @date 2021/1/30
// * @Description
// */
//public class FastDfsOssApiClient extends BaseApiClient {
//
//    private String serverUrl;
//    private String domainUrl;
//
//    public FastDfsOssApiClient() {
//        super("FastDFS");
//    }
//
//    public FastDfsOssApiClient init(String serverUrl, String domainUrl) {
//        this.serverUrl = serverUrl;
//        this.domainUrl = domainUrl;
//        return this;
//    }
//
//    @Override
//    public VirtualFile uploadFile(InputStream is, String imageUrl) {
//        Date startTime = new Date();
//        String key = FileUtil.generateTempFileName(imageUrl);
//        this.createNewFileName(key);
//        try{
//            final FastdfsClientUtil fastdfsClientUtil = new FastdfsClientUtil();
//            final byte[] bytes = new byte[is.available()];
//            is.
//            final int read = is.read(bytes);
//            final String s = fastdfsClientUtil.upload(is, imageUrl);
//            return new VirtualFile()
//                    .setOriginalFileName(key)
//                    .setSuffix(this.suffix)
//                    .setUploadStartTime(startTime)
//                    .setUploadEndTime(new Date())
//                    .setFilePath(this.newFileName)
//                    .setFullFilePath(this.path + this.newFileName);
//        } catch (IOException ex) {
//            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + ex.getMessage());
//        }
//    }
//
//    @Override
//    public boolean removeFile(String key) {
//        if (StringUtils.isNullOrEmpty(key)) {
//            throw new OssApiException("[" + this.storageType + "]删除文件失败：文件key为空");
//        }
//        UpaiManager upaiManager = new UpaiManager(bucket, operatorName, operatorPwd);
//        // 删除文件
//        try {
//            upaiManager.deleteFile(key, null);
//            return true;
//        } catch (IOException e) {
//            throw new ServiceException("[" + this.storageType + "]文件删除失败：" + e.getMessage());
//        }
//    }
//
//    @Override
//    public VirtualFile multipartUpload(File file) {
//        return null;
//    }
//
//    @Override
//    protected void check() {
//
//    }
//
//    @Override
//    public InputStream downloadFileStream(String key) {
//        return null;
//    }
//}
