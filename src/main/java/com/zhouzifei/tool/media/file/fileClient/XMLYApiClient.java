package com.zhouzifei.tool.media.file.fileClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.FileUtil;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.HttpUtils;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/10/12
 * @Description
 */
public class XMLYApiClient  extends BaseApiClient {

    private static String token;
    private String requestUrl = "https://upload.ximalaya.com/dtres/headerThumb/upload";

    public XMLYApiClient() {
        super("喜马拉雅");
    }
    public XMLYApiClient init(String accessKey, String secretKey, String token, String uploadType) {
        if (StringUtils.isEmpty(token)) {
//            //获取token
//            final String s1 = "username="+accessKey+"&password="+secretKey;
//            final String s = HttpNewUtils.DataPost(requestUrl + "/token", s1);
//            final JSONObject jsonObject = JSONObject.parseObject(s);
//            if(!(Boolean) jsonObject.get("success")){
                throw new ServiceException("[" + this.storageType + "]初始化失败");
//            }
//            final JSONObject data = (JSONObject)jsonObject.get("data");
//            final Object token1 = data.get("token");
//            XMLYApiClient.token = (String) token1;
        }else{
            XMLYApiClient.token = "1&_token="+token+";&"+token;
            super.folder = StringUtils.isEmpty(uploadType) ? "" : uploadType + "/";
        }
        return this;
    }
    @Override
    public VirtualFile uploadFile(MultipartFile file) {
        try(InputStream inputStream= file.getInputStream();) {
            final String filename = file.getOriginalFilename();
            return uploadFile(inputStream,filename);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(File file) {
        try(InputStream inputStream= new FileInputStream(file);) {
            final String filename = file.getName();
            return uploadFile(inputStream,filename);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(InputStream inputStream, String fileName) {
        try (InputStream uploadIs = StreamUtil.clone(inputStream);
             InputStream fileHashIs = StreamUtil.clone(inputStream)) {
            final String suffix = FileUtil.getSuffix(fileName);
            final Date startTime = new Date();
            Map<String, String> hears = new HashMap<>();
            hears.put("Cookie", token);
            String result = HttpUtils.postFile(requestUrl, hears, "file", uploadIs, fileName);
            final JSONObject parse = JSONObject.parseObject(result);
            if (null == parse) {
                throw new RuntimeException();
            }
            final Object data = parse.get("data");
            if (null == data) {
                throw new RuntimeException();
            }
            final Object url = ((JSONArray) data).get(0);
            if (null == url) {
                throw new RuntimeException();
            }
            final String url1 = (String) ((JSONObject) url).get("url");
            final String filePath = (String) ((JSONObject) url).get("dfsId");
            return new VirtualFile()
                    .setOriginalFileName(fileName)
                    .setSuffix(suffix)
                    .setUploadStartTime(startTime)
                    .setUploadEndTime(new Date())
                    .setFilePath(filePath)
                    .setFileHash(DigestUtils.md5DigestAsHex(fileHashIs))
                    .setFullFilePath(url1)
                    .setSize(fileHashIs.available());
        }catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        throw new ServiceException("[" + this.storageType + "]图床文件无法删除：");
    }

    @Override
    public void downloadFile(String fileName, String localFile) {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        return null;
    }

    @Override
    public VirtualFile saveToCloudStorage(String fileUrl, String referer, String fileName) {
        return null;
    }

    @Override
    protected void check() {

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
    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
        return null;
    }
}
