package com.zhouzifei.tool.fileClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.SmmsFileProperties;
import com.zhouzifei.tool.dto.SmmFileList;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.HttpUtils;
import com.zhouzifei.tool.util.RandomsUtil;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private String requestUrl = "https://sm.ms/api/v2";

    public SmMsApiClient() {
        super("阿里云OSS");
    }

    public SmMsApiClient(FileProperties fileProperties) {
        super("SMMS");
        init(fileProperties);
    }

    @Override
    public SmMsApiClient init(FileProperties fileProperties) {
        final SmmsFileProperties smmsFileProperties = (SmmsFileProperties) fileProperties;
        this.username = smmsFileProperties.getUserName();
        this.password = smmsFileProperties.getPassWord();
        this.token = smmsFileProperties.getToken();
        if (StringUtils.isEmpty(token)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new ServiceException("[" + this.storageType + "]初始化失败！");
            }
            //获取token
            final String s1 = "username=" + username + "&password=" + password;
            final String s = HttpUtils.DataPost(this.domainUrl + "token", s1);
            final JSONObject jsonObject = JSONObject.parseObject(s);
            if (!(Boolean) jsonObject.get("success")) {
                throw new ServiceException("[" + this.storageType + "]初始化失败：" + jsonObject);
            }
            final JSONObject data = (JSONObject) jsonObject.get("data");
            final Object token1 = data.get("token");
            this.token = (String) token1;
        }
        hears.put("token", token);
        hears.put("Content-Type", "multipart/form-data");
        checkDomainUrl(requestUrl);
        return this;
    }

    @Override
    public String uploadInputStream(InputStream is, String imageUrl) {
        this.check();
        Date startTime = new Date();
        try (InputStream uploadIs = StreamUtil.clone(is); InputStream fileHashIs = StreamUtil.clone(is)) {
            final String fileName = RandomsUtil.alpha(20) + ".jpg";
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "========7d4a6d158c9";
            // 服务器的域名
            URL url = new URL(this.domainUrl + "/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 上传文件
            File file = new File(fileName);
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"smfile\";filename=\"" + fileName + "\"" + newLine);
            sb.append("Content-Type:multipart/form-data");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);
            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = is.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            // 最后添加换行
            out.write(newLine.getBytes());
            is.close();
            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            final JSONObject parse = JSONObject.parseObject(result.toString());
            final Object data = parse.get("data");
            final JSONObject dataJosn = JSONObject.parseObject((String) data);
            final Object newFileUrl = dataJosn.get("url");
            return imageUrl;
        } catch (IOException e) {
            throw new ServiceException("[" + this.storageType + "]文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        throw new ServiceException("[" + this.storageType + "]文件上传失败：该上传类型不支持删除");
    }

    @Override
    public VirtualFile multipartUpload(InputStream inputStream, MetaDataRequest metaDataRequest) {
        throw new ServiceException("[" + this.storageType + "]文件上传失败：该上传类型不支持分片上传");
    }

    @Override
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("Content-Type", "multipart/form-data");
        map.put("Authorization", token);
        final String resultString = HttpUtils.Get(this.domainUrl + "upload_history", map);
        if (StringUtils.isEmpty(resultString)) {
            return new ArrayList<>();
        }
        final JSONObject jsonObject = JSONObject.parseObject(resultString);
        final Boolean success = jsonObject.getBoolean("success");
        if (!success) {
            return new ArrayList<>();
        }
        final JSONArray jsonArray = jsonObject.getJSONArray("data");
        final List<SmmFileList> smmFileLists = jsonArray.toJavaList(SmmFileList.class);
        List<VirtualFile> virtualFiles = new ArrayList<>();
        for (SmmFileList smmFileList : smmFileLists) {
            final VirtualFile file = VirtualFile.builder()
                    .fileHash(smmFileList.getHash())
                    .filePath(smmFileList.getPath())
                    .fullFilePath(smmFileList.getUrl())
                    .originalFileName(smmFileList.getFilename())
                    .suffix(FileUtil.getSuffixName(smmFileList.getFilename()))
                    .size(Long.parseLong(smmFileList.getSize()))
                    .isFold(false)
                    .uploadStartTime(new Date(smmFileList.getCreated_at()))
                    .uploadEndTime(new Date(smmFileList.getCreated_at()))
                    .build();
            virtualFiles.add(file);
        }
        return virtualFiles;
    }

    @Override
    public boolean exists(String fileName) {
        return false;
    }

    @Override
    protected void check() {

    }

    @Override
    public InputStream downloadFileStream(String key) {
        return null;
    }

    @Override
    public ApiClient getAwsApiClient() {
        throw new com.zhouzifei.tool.common.ServiceException("[" + this.storageType + "]暂不支持AWS3协议！");
    }
}
