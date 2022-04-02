package com.zhouzifei.tool.fileClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.GithubFileProperties;
import com.zhouzifei.tool.dto.GithubFileList;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.HttpUtils;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
        checkDomainUrl(domianUrl);
        return this;
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
            jsonObject.put("message", fileName);
            jsonObject.put("content", baseContent);
            final String toString = jsonObject.toString();
            String uploadUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + fileName;
            //查询是否存在
            if (exists(fileName)) {
                jsonObject.put("sha", UUID.randomUUID().toString().replace("-", ""));
            }
            final String s = HttpUtils.JsonPut(uploadUrl, toString, hears);
            return user + "/" + repository + "/" + fileName;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        Map<String, String> hears = new HashMap<>();
        hears.put("Authorization", "Bearer " + token);
        hears.put("Content-Type", "text/plain");
        hears.put("Accept","application/vnd.github.v3+json");
        String deleteUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + fileName;
        //查询是否存在
        final String get = HttpUtils.Get(deleteUrl, hears);
        if (null == get) {
            throw new ServiceException("[" + this.storageType + "]文件不存在：" + fileName);
        }
        final JSONArray objects = JSONObject.parseArray(get);
        final List<GithubFileList> githubFileLists = objects.toJavaList(GithubFileList.class);
        for (GithubFileList githubFileList : githubFileLists) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "删除文件:"+fileName);
            jsonObject.put("sha", githubFileList.getSha());
//            jsonObject.put("committer", githubFileList.getSha());
//            jsonObject.put("sha", githubFileList.getSha());
            final String toString = jsonObject.toString();
            final String s = HttpUtils.JsonDelete(deleteUrl, toString, hears);
        }
        return true;
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
        Map<String, String> hears = new HashMap<>();
        hears.put("Authorization", "Bearer " + token);
        hears.put("Content-Type", "text/plain");
        hears.put("Accept","application/vnd.github.v3+json");
        final String listRequesrFold = fileListRequesr.getFold();
        String uploadUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + listRequesrFold;
        //查询是否存在
        final String get = HttpUtils.Get(uploadUrl, hears);
        if (null == get) {
            return new ArrayList<>();
        }
        final JSONArray objects = JSONObject.parseArray(get);
        final List<GithubFileList> githubFileLists = objects.toJavaList(GithubFileList.class);
        List<VirtualFile> virtualFiles = new ArrayList<>();
        for (GithubFileList githubFileList : githubFileLists) {
            final VirtualFile file = VirtualFile.builder()
                    .fileHash(githubFileList.getSha())
                    .filePath(listRequesrFold + githubFileList.getPath())
                    .fullFilePath(githubFileList.getDownload_url())
                    .originalFileName(githubFileList.getName())
                    .suffix(FileUtil.getSuffixName(githubFileList.getName()))
                    .size(Long.parseLong(githubFileList.getSize()))
                    .isFold(!githubFileList.getType().equals("file"))
                    .build();
            virtualFiles.add(file);
        }
        return virtualFiles;
    }

    @Override
    public boolean exists(String fileName) {
        Map<String, String> hears = new HashMap<>();
        hears.put("Authorization", "Bearer " + token);
        hears.put("Content-Type", "text/plain");
        String uploadUrl = requestUrl + "repos/" + user + "/" + repository + "/contents/" + fileName;
        //查询是否存在
        final String get = HttpUtils.Get(uploadUrl, hears);
        if (null == get) {
            return false;
        }
        return true;
    }
    @Override
    public ApiClient getAwsApiClient(){
        throw new ServiceException("[" + this.storageType + "]暂不支持AWS3协议！");
    }
}
