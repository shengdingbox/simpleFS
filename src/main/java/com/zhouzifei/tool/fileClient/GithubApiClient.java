package com.zhouzifei.tool.fileClient;

import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.GithubFileProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.HttpUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 周子斐
 * @date 2021/10/12
 * @Description
 */
public class GithubApiClient extends BaseApiClient {

    private String token;
    private String user;
    private String repository;
    private final String requestUrl = "https://api.github.com/";
    private final String domianUrl = "https://cdn.jsdelivr.net/gh/";

    public GithubApiClient() {
        super("github");
    }
    public GithubApiClient(FileProperties fileProperties) {
        super("github");
        init(fileProperties);
    }

    @Override
    public GithubApiClient init(FileProperties fileProperties) {
        final GithubFileProperties githubFileProperties = fileProperties.getGithub();
        this.repository = githubFileProperties.getRepository();
        this.token = githubFileProperties.getToken();
        this.user = githubFileProperties.getUser();
        return this;
    }

    @Override
    public VirtualFile uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();) {
            final String filename = file.getOriginalFilename();
            return uploadFile(inputStream, filename);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public VirtualFile uploadFile(File file) {
        try (InputStream inputStream = new FileInputStream(file);) {
            final String filename = file.getName();
            return uploadFile(inputStream, filename);
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String uploadInputStream(InputStream inputStream, String fileName) {
        try (InputStream uploadIs = StreamUtil.clone(inputStream)) {
            Map<String, String> hears = new HashMap<>();
            hears.put("Authorization", "Bearer " + token);
            hears.put("Content-Type", "text/plain");
            // 读取图片字节数组
            byte[] data = new byte[uploadIs.available()];
            uploadIs.read(data);
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            final String baseContent = encoder.encode(data);// 返回Base64编码过的字节数组字符串
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("message",fileName);
            jsonObject.put("content",baseContent);
            final String toString = jsonObject.toString();
            String uploadUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + fileName;
            //查询是否存在
            if(exists(fileName)){
                jsonObject.put("sha", UUID.randomUUID().toString().replace("-",""));
            }
            final String s = HttpUtils.JsonPut(uploadUrl, toString, hears);
            return domianUrl + user + "/" + repository +"/"+  fileName;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        throw new ServiceException("[" + this.storageType + "]图床文件无法删除：");
    }

    @Override
    public void downloadFileToLocal(String fileName, String localFile) {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        return FileUtil.getInputStreamByUrl(key, requestUrl);
    }

    @Override
    protected void check() {

    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        throw new ServiceException("[" + this.storageType + "]文件上传失败：该上传类型不支持分片上传");
    }

    @Override
    public VirtualFile resumeUpload(InputStream inputStream, String fileName) {
        return null;
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        Map<String, String> hears = new HashMap<>();
        hears.put("Authorization", "Bearer " + token);
        hears.put("Content-Type", "text/plain");
        String uploadUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + fileName;
        //查询是否存在
        final String get = HttpUtils.Get(uploadUrl, hears);
        if (null == get){
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream("/Users/Dabao/Downloads/123.png");
        final FileProperties fileProperties = new FileProperties();
        final GithubFileProperties githubFileProperties = fileProperties.getGithub();
        githubFileProperties.setToken("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");
        githubFileProperties.setUser("shengdingbox");
        githubFileProperties.setRepository("static");
        final GithubApiClient githubApiClient = new GithubApiClient(fileProperties);
        final String s = githubApiClient.uploadInputStream(fileInputStream, "123.png");
        System.out.println(s);
    }
}
