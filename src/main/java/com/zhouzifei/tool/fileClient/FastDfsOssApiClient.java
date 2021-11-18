package com.zhouzifei.tool.fileClient;


import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.utils.CollectionUtils;
import com.qcloud.cos.utils.IOUtils;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.common.fastdfs.*;
import com.zhouzifei.tool.common.fastdfs.common.NameValuePair;
import com.zhouzifei.tool.consts.UpLoadConstant;
import com.zhouzifei.tool.dto.CheckFileResult;
import com.zhouzifei.tool.dto.FileResult;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.RedisUtil;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

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
        createNewFileName(imageUrl);
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
                    .setOriginalFileName(this.newFileName)
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(fullPath)
                    .setFullFilePath(this.domainUrl + "/" + fullPath);
        } catch (IOException var6) {
            log.info("上传失败,失败原因{}", var6.getMessage());
            throw new ServiceException("文件上传异常!");
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
        }
        //tracker 客户端
        TrackerClient trackerClient = new TrackerClient();
        //获取trackerServer
        try {
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            //创建StorageClient 对象
            StorageClient storageClient = new StorageClient(trackerServer);
            final String group = getGroup(fileName);
            final String filePath = getFilePath(fileName);
            FileInfo fileInfo = storageClient.query_file_info(group, filePath);
            if(null != fileInfo){
                //文件元数据信息组
                final int deleteFile = storageClient.delete_file(group, filePath);
                return !(deleteFile == 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        return null;
    }

    @Override
    public VirtualFile multipartUpload(MultipartFile file, MetaDataRequest metaDataRequest, HttpServletRequest request) {
        Date startTime = new Date();
        final String fileName = file.getOriginalFilename();
        this.createNewFileName(fileName);
        String fileMd5 = metaDataRequest.getFileMd5();
        String noGroupPath;//存储在fastdfs不带组的路径
        String chunklockName = UpLoadConstant.chunkLock + fileMd5;
        boolean currOwner = false;//真正的拥有者
        String chunk = metaDataRequest.getChunk();
        String chunks = metaDataRequest.getChunks();
        try {
            String userName = (String) request.getSession().getAttribute("name");
            if (StringUtils.isEmpty(userName)) {
                request.getSession().setAttribute("name", "yxqy");
            }
            if (StringUtils.isEmpty(chunk)) {
                metaDataRequest.setChunk("0");
                chunk = "0";
            }
            if (StringUtils.isEmpty(chunks)) {
                metaDataRequest.setChunks("1");
                chunks = "1";
            }
            Long lock = RedisUtil.incrBy(chunklockName, 1);
            if (lock > 1) {
                throw new ServiceException("请求块锁失败");
            }
            //写入锁的当前拥有者
            currOwner = true;
            BufferedOutputStream stream = null;
            String chunkCurrkey = UpLoadConstant.chunkCurr + fileMd5; //redis中记录当前应该穿第几块(从0开始)
            String chunkCurr = RedisUtil.getString(chunkCurrkey);
            noGroupPath = "";
            Integer chunkSize = metaDataRequest.getChunkSize();
            if (StringUtils.isEmpty(chunkCurr)) {
               RedisUtil.setString(chunkCurrkey,"0");
               chunkCurr = "0";
            }
            Integer chunkCurr_int=Integer.parseInt(chunkCurr);
            Integer chunk_int = Integer.parseInt(chunk);
            if (chunk_int < chunkCurr_int) {
                throw new ServiceException("当前文件块已上传");
            } else if (chunk_int > chunkCurr_int) {
                throw new ServiceException("当前文件块需要等待上传,稍后请重试");
            }
            log.info("***********开始上传**********");
            String path = null;
            if (!file.isEmpty()) {
                try {
                    //获取已经上传文件大小
                    Long historyUpload = 0L;
                    String historyUploadStr = RedisUtil.getString(UpLoadConstant.historyUpload + fileMd5);
                    if (StringUtils.isNotEmpty(historyUploadStr)) {
                        historyUpload = Long.parseLong(historyUploadStr);
                    }
                    log.info("historyUpload大小:" + historyUpload);
                    byte[] bytes = file.getBytes();
                    long fileSize = file.getSize();
                    if (chunk_int == 0) {
                        RedisUtil.setString(chunkCurrkey, String.valueOf(chunkCurr_int + 1));
                        log.info(chunk + ":redis块+1");
                        try {
                            //tracker 客户端
                            TrackerClient trackerClient = new TrackerClient();
                            //获取trackerServer
                            TrackerServer trackerServer = trackerClient.getTrackerServer();
                            //创建StorageClient 对象
                            StorageClient storageClient = new StorageClient(trackerServer);
                            //文件元数据信息组
                            NameValuePair[] nameValuePairs = {new NameValuePair("author", "huhy")};
                            String suffix = FileUtil.getSuffix(fileName);
                            String[] strings = storageClient.upload_appender_file(UpLoadConstant.DEFAULT_GROUP,bytes,0,(int)fileSize,suffix,nameValuePairs);
                            path = strings[1];
                            noGroupPath = path;
                            log.info(chunk + ":更新完fastdfs");
                            if (noGroupPath == null) {
                                RedisUtil.setString(chunkCurrkey, String.valueOf(chunkCurr_int));
                                throw new ServiceException("获取远程文件路径出错");
                            }

                        } catch (Exception e) {
                            RedisUtil.setString(chunkCurrkey, String.valueOf(chunkCurr_int));
                            // e.printStackTrace();
                            //还原历史块
                            log.error("初次上传远程文件出错", e);
                            throw new ServiceException("上传远程服务器文件出错");
                        }
                        RedisUtil.setString(UpLoadConstant.fastDfsPath + fileMd5, path);
                        log.info("上传文件 result={}", path);
                    } else {
                        RedisUtil.setString(chunkCurrkey, String.valueOf(chunkCurr_int + 1));
                        log.info(chunk + ":redis块+1");
                        noGroupPath = RedisUtil.getString(UpLoadConstant.fastDfsPath + fileMd5);
                        if (noGroupPath == null) {
                            throw new ServiceException("无法获取上传远程服务器文件出错");
                        }
                        try {
                            //tracker 客户端
                            TrackerClient trackerClient = new TrackerClient();
                            //获取trackerServer
                            TrackerServer trackerServer = trackerClient.getTrackerServer();
                            //创建StorageClient 对象
                            StorageClient storageClient = new StorageClient(trackerServer);
                            //追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                            storageClient.append_file(UpLoadConstant.DEFAULT_GROUP, noGroupPath, bytes, 0, (int)fileSize);
                            log.info(chunk + ":更新完fastdfs");
                        } catch (Exception e) {
                            RedisUtil.setString(chunkCurrkey, String.valueOf(chunkCurr_int));
                            log.error("更新远程文件出错", e);
                            //   e.printStackTrace();
                            //  throw  new RuntimeException("初次上传远程文件出错");
                            throw new ServiceException("更新远程文件出错");
                        }
                    }
                    //修改历史上传大小
                    historyUpload = historyUpload + fileSize;
                    RedisUtil.setString(UpLoadConstant.historyUpload + fileMd5, String.valueOf(historyUpload));
                    //最后一块,清空upload,写入数据库
                    Long size = metaDataRequest.getSize();
                    Integer chunks_int = Integer.parseInt(metaDataRequest.getChunks());
                    if (chunk_int + 1 == chunks_int) {
                        //持久化上传完成文件,也可以存储在mysql中
                        FileResult fileResult = new FileResult();
                        fileResult.setMd5(fileMd5);
                        fileResult.setName(fileName);
                        fileResult.setLenght(size);
                        fileResult.setUrl(UpLoadConstant.DEFAULT_GROUP + "/" + noGroupPath);
                        RedisUtil.rpush(UpLoadConstant.completedList, JSONObject.toJSONString(fileResult));
                        RedisUtil.delKeys(new String[]{UpLoadConstant.chunkCurr + fileMd5,
                                UpLoadConstant.fastDfsPath + fileMd5,
                                UpLoadConstant.currLocks + fileMd5,
                                UpLoadConstant.lockOwner + fileMd5
                        });
                    }
                } catch (Exception e) {
                    log.error("上传文件错误", e);
                    e.printStackTrace();
                    //throw new ServiceException("上传错误 " + e.getMessage());
                }
            }
        } finally {
            //锁的当前拥有者才能释放块上传锁
            if (currOwner) {
                RedisUtil.setString(chunklockName, "0");
            }
        }
        log.info("***********结束**********");
        return new VirtualFile()
                .setOriginalFileName(fileName)
                .setFileHash(fileMd5)
                .setSuffix(this.suffix)
                .setUploadStartTime(startTime)
                .setUploadEndTime(new Date())
                .setFilePath(UpLoadConstant.DEFAULT_GROUP + "/" + noGroupPath)
                .setFullFilePath(this.serverUrl + UpLoadConstant.DEFAULT_GROUP + "/" + noGroupPath);

    }

    @Override
    public CheckFileResult checkFile(MetaDataRequest metaDataRequest, HttpServletRequest request) {
        //storageClient.deleteFile(UpLoadConstant.DEFAULT_GROUP, "M00/00/D1/eSqQlFsM_RWASgIyAAQLLONv59s385.jpg");
        String userName = (String) request.getSession().getAttribute("name");
        if (StringUtils.isEmpty(userName)) {
            request.getSession().setAttribute("name", "yxqy");
        }
        String fileMd5 = metaDataRequest.getFileMd5();
        if (StringUtils.isEmpty(fileMd5)) {
            throw new ServiceException("fileMd5不能为空");
        }
        CheckFileResult checkFileResult = new CheckFileResult();
        //模拟从mysql中查询文件表的md5,这里从redis里查询
        List<String> fileList = RedisUtil.getListAll(UpLoadConstant.completedList);
        if (!CollectionUtils.isNullOrEmpty(fileList)) {
            for (String e : fileList) {
                JSONObject obj = JSONObject.parseObject(e);
                String md5 = StringUtils.isEmpty(obj.getString("md5")) ? "0" : obj.getString("md5");
                if (md5.equals(fileMd5)) {
                    checkFileResult.setTotalSize(obj.getLong("lenght"));
                    checkFileResult.setViewPath(obj.getString("url"));
                    return checkFileResult;
                }
            }
        }
        //查询锁占用
        String lockName = UpLoadConstant.currLocks + fileMd5;
        Long lock = RedisUtil.incrBy(lockName, 1);
        String lockOwner = UpLoadConstant.lockOwner + fileMd5;
        String chunkCurrkey = UpLoadConstant.chunkCurr + fileMd5;
        if (lock > 1) {
            checkFileResult.setLock(1);
            //检查是否为锁的拥有者,如果是放行
            String oWner = RedisUtil.getString(lockOwner);
            if (StringUtils.isEmpty(oWner)) {
                throw new ServiceException("无法获取文件锁拥有者");
            } else {
                if (oWner.equals(request.getSession().getAttribute("name"))) {
                    String chunkCurr = RedisUtil.getString(chunkCurrkey);
                    if (StringUtils.isEmpty(chunkCurr)) {
                        throw new ServiceException("无法获取当前文件chunkCurr");
                    }
                    checkFileResult.setChunkCurr(Integer.parseInt(chunkCurr));
                    return checkFileResult;
                } else {
                    throw new ServiceException("当前文件已有人在上传,您暂无法上传该文件");
                }
            }
        } else {
            //初始化锁.分块
            RedisUtil.setString(lockOwner, (String) request.getSession().getAttribute("name"));
            RedisUtil.setString(chunkCurrkey, "0"); //第一块索引是0,与前端保持一致
            checkFileResult.setChunkCurr(0);
            return checkFileResult;
        }
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

    public static String getGroup(String fileName) {
        final String[] split = fileName.split("/");
        return split[0];
    }

    public static String getFilePath(String fileName) {
        final String[] split = fileName.split("/");
        List<String> strings = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
        return String.join("/", strings);
    }

    @Override
    public InputStream downloadFileStream(String fileName) {
        try {
            //tracker 客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取trackerServer
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            //创建StorageClient 对象
            StorageClient storageClient = new StorageClient(trackerServer);
            final String group = getGroup(fileName);
            final String filePath = getFilePath(fileName);
            FileInfo fileInfo = storageClient.query_file_info(group, filePath);
            if(null == fileInfo){
                throw new ServiceException("文件不存在");
            }
            byte[] bytes = storageClient.download_file(group, filePath);
            return new ByteArrayInputStream(bytes);
        } catch (IOException var6) {
            log.info("上传失败,失败原因{}", var6.getMessage());
            throw new ServiceException("文件上传异常!");
        }
    }

    public static void main(String[] args) {
        String historyUploadStr = "213902139";
        long historyUpload = Long.parseLong(historyUploadStr);
        System.out.println(historyUpload);
    }
}
