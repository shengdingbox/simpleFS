package com.zhouzifei.tool.media.file.fileClient;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.*;
import com.google.gson.JsonObject;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.html.util.Randoms;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.HttpData;
import com.zhouzifei.tool.util.HttpUtils;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月23日
 * @since 1.0
 */
@Slf4j
public class SmMsApiClient extends BaseApiClient {

    private Map<String, String> hears = new HashMap<>();
    private String token;
    private String url = "https://sm.ms/api/v2";

    public SmMsApiClient() {
        super("阿里云OSS");
    }

    public SmMsApiClient init(String accessKey, String secretKey, String token, String uploadType) {
        this.token = token;
        super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        if (StringUtils.isEmpty(token)) {
            //获取token
            Map<String, String> map = new HashMap<>();
            map.put("username", accessKey);
            map.put("password", secretKey);
            final HttpResponse httpResponse;
            try {
                httpResponse = HttpUtils.doPost(url, "/token", HttpUtils.nullMap, HttpUtils.nullMap, map);
                final HttpEntity entity = httpResponse.getEntity();
                final String s = EntityUtils.toString(entity);
                final JSONObject jsonObject = JSONObject.parseObject(s);
                final Object data = jsonObject.get("data");
                final JSONObject dataJson = JSONObject.parseObject((String) data);
                final Object token1 = dataJson.get("token");
                this.token = (String) token1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hears.put("token", this.token);
        hears.put("Content-Type", "multipart/form-data");
        return this;
    }

    @Override
    public VirtualFile uploadFile(InputStream is, String imageUrl) {
        this.check();
        Date startTime = new Date();
        try (InputStream uploadIs = StreamUtil.clone(is);
             InputStream fileHashIs = StreamUtil.clone(is)) {
            final String fileName = Randoms.alpha(6) + "/" + Randoms.alpha(20) + ".jpg";
            String result = HttpUtils.postFile(url + "/upload"
                    , hears
                    , uploadIs, fileName);
            final JSONObject parse = JSONObject.parseObject(result);
            final Object data = parse.get("data");
            final JSONObject dataJosn = JSONObject.parseObject((String) data);
            final Object newFileUrl = dataJosn.get("url");
            return new VirtualFile()
                    .setOriginalFileName(FileUtil.getName(imageUrl))
                    .setSuffix(this.suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(this.newFileName)
                    .setFileHash(DigestUtils.md5DigestAsHex(fileHashIs))
                    .setFullFilePath((String) newFileUrl);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        return false;
    }

    @Override
    public VirtualFile multipartUpload(File file) {
        return null;
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, String fileName) {
        return null;
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        return null;
    }

    //    private String getName(String imageUrl) {
//        if (!this.client.doesBucketExist(bucketName)) {
//            throw new ServiceException("[阿里云OSS] 无法上传文件！Bucket不存在：" + bucketName);
//        }
//        boolean exists = this.client.doesObjectExist(bucketName, imageUrl);
//        if (exists) {
//            this.suffix = FileUtil.getSuffix(imageUrl);
//            imageUrl = Randoms.alpha(16) + this.suffix;
//        }
//        this.createNewFileName(imageUrl);
//        return imageUrl;
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param fileName OSS中保存的文件名
//     */
//    @Override
//    public boolean removeFile(String fileName) {
//        this.check();
//        if (StringUtils.isEmpty(fileName)) {
//            throw new ServiceException("[" + this.storageType + "]删除文件失败：文件key为空");
//        }
//        try {
//            boolean exists = this.client.doesBucketExist(bucketName);
//            if (!exists) {
//                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
//            }
//            if (!this.client.doesObjectExist(bucketName, fileName)) {
//                throw new ServiceException("[阿里云OSS] 文件删除失败！文件不存在：" + bucketName + "/" + fileName);
//            }
//            this.client.deleteObject(bucketName, fileName);
//            return true;
//        } catch (Exception e) {
//            throw new ServiceException(e.getMessage());
//        } finally {
//            this.client.shutdown();
//        }
//    }
//
//    @Override
//    public VirtualFile multipartUpload(File file) {
//        final String fileName = file.getName();
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            return multipartUpload(fileInputStream, fileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public VirtualFile multipartUpload(InputStream inputStream, String fileName) {
//        this.check();
//        final Date startDate = new Date();
//        fileName = getName(fileName);
//        // 创建InitiateMultipartUploadRequest对象。
//        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, this.newFileName);
//        // 初始化分片。
//        InitiateMultipartUploadResult upresult = client.initiateMultipartUpload(request);
//        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
//        String uploadId = upresult.getUploadId();
//        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
//        List<PartETag> partETags = new ArrayList<PartETag>();
//        // 计算文件有多少个分片。
//        final long partSize = 1024 * 1024L;
//        long fileLength = 0;
//        try {
//            fileLength = inputStream.available();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int partCount = (int) (fileLength / partSize);
//        if (fileLength % partSize != 0) {
//            partCount++;
//        }
//        progressListener.start(this.newFileName);
//        // 遍历分片上传。
//        for (int i = 0; i < partCount; i++) {
//            long startPos = i * partSize;
//            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
//            //控制输出小数点后的位数
//            DecimalFormat df = new DecimalFormat("#.##");
//            float f = (i / (float) partCount) * 100;
//            log.info("已下载：" + df.format(f) + "%\t\t");
//            try (InputStream instream = StreamUtil.clone(inputStream)) {
//                // 跳过已经上传的分片。
//                instream.skip(startPos);
//                UploadPartRequest uploadPartRequest = new UploadPartRequest();
//                uploadPartRequest.setBucketName(bucketName);
//                uploadPartRequest.setKey(this.newFileName);
//                uploadPartRequest.setUploadId(uploadId);
//                uploadPartRequest.setInputStream(instream);
//                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
//                uploadPartRequest.setPartSize(curPartSize);
//                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
//                uploadPartRequest.setPartNumber(i + 1);
//                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
//                UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
//                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
//                partETags.add(uploadPartResult.getPartETag());
//                progressListener.process(i, partCount);
//            } catch (Exception e) {
////                log.info(e.toString());
////                throw new ServiceException("[" + this.storageType + "]文件分片上传失败：" + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        // 创建CompleteMultipartUploadRequest对象。
//        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
//        CompleteMultipartUploadRequest completeMultipartUploadRequest =
//                new CompleteMultipartUploadRequest(bucketName, this.newFileName, uploadId, partETags);
//        // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
//        // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
//        CompleteMultipartUploadResult completeMultipartUploadResult = client.completeMultipartUpload(completeMultipartUploadRequest);
//        // 关闭OSSClient。
//        client.shutdown();
//        final VirtualFile virtualFile = new VirtualFile()
//                .setOriginalFileName(this.newFileName)
//                .setSuffix(suffix)
//                .setUploadStartTime(startDate)
//                .setUploadEndTime(new Date())
//                .setFilePath(this.newFileName)
//                .setFileHash("")
//                .setFullFilePath(this.domainUrl + this.newFileName);
//        progressListener.end(virtualFile);
//        return virtualFile;
//    }
//
//    @Override
//    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
//        return null;
//    }
//
//    @Override
//    protected void check() {
//        if (StringUtils.isNullOrEmpty(token)) {
//            throw new ServiceException("[" + this.storageType + "]尚未配置sm.ms，文件上传功能暂时不可用！");
//        }
//    }
//
//    /**
//     * 下载文件
//     *
//     * @param fileName OSS中保存的文件名
//     */
//    @Override
//    public InputStream downloadFileStream(String fileName) {
//        this.check();
//        if (StringUtils.isEmpty(fileName)) {
//            throw new ServiceException("[" + this.storageType + "]下载文件失败：文件key为空");
//        }
//        try {
//            boolean exists = this.client.doesBucketExist(bucketName);
//            if (!exists) {
//                throw new ServiceException("[阿里云OSS] 文件删除失败！Bucket不存在：" + bucketName);
//            }
//            if (!this.client.doesObjectExist(bucketName, fileName)) {
//                throw new ServiceException("[阿里云OSS] 文件下载失败！文件不存在：" + bucketName + "/" + fileName);
//            }
//            OSSObject object = this.client.getObject(bucketName, fileName);
//            return object.getObjectContent();
//        } finally {
//            this.client.shutdown();
//        }
//    }
    public static void main(String[] args) throws IOException {
        final File file = new File("/Users/Dabao/Downloads/vycqrhm2g3ryix7x.gif");
        final FileInputStream fileInputStream = new FileInputStream(file);
        final String s = DigestUtils.md5DigestAsHex(fileInputStream);
        System.out.println(s);
    }
}
