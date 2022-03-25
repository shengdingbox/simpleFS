package com.zhouzifei.tool.fileClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.zhouzifei.tool.config.FileProperties;
import com.zhouzifei.tool.config.XmlyFileProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.entity.FileListRequesr;
import com.zhouzifei.tool.entity.MetaDataRequest;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import com.zhouzifei.tool.util.FileUtil;
import com.zhouzifei.tool.util.HttpUtils;
import com.zhouzifei.tool.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/10/12
 * @Description
 */
public class XMLYApiClient extends BaseApiClient {

    private static String token;
    private final String requestUrl = "https://upload.ximalaya.com/dtres/headerThumb/upload";

    public XMLYApiClient() {
        super("喜马拉雅");
    }
    public XMLYApiClient(FileProperties fileProperties) {
        super("喜马拉雅");
        init(fileProperties);
    }

    @Override
    public XMLYApiClient init(FileProperties fileProperties) {
        final XmlyFileProperties xmlyFileProperties = fileProperties.getXmly();
        String xmlyCookie = xmlyFileProperties.getCookies();
        if (StringUtils.isEmpty(xmlyCookie)) {
//            //获取token
//            final String s1 = "username="+accessKey+"&password="+secretKey;
//            final String s = HttpUtils.DataPost(requestUrl + "/token", s1);
//            final JSONObject jsonObject = JSONObject.parseObject(s);
//            if(!(Boolean) jsonObject.get("success")){
            throw new ServiceException("[" + this.storageType + "]初始化失败");
//            }
//            final JSONObject data = (JSONObject)jsonObject.get("data");
//            final Object token1 = data.get("token");
//            XMLYApiClient.token = (String) token1;
        } else {
            XMLYApiClient.token = "1&_token=" + xmlyCookie + ";&" + xmlyCookie;
        }
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
            hears.put("Cookie", token);
            String result = HttpUtils.filePost(requestUrl, hears, "file", uploadIs, fileName);
            final JSONObject parse = JSONObject.parseObject(result);
            if (null == parse) {
                throw new RuntimeException();
            }
            final Boolean status = parse.getBoolean("status");
            if (!status) {
                final String msg = parse.getString("msg");
                throw new ServiceException(msg);
            }
            final Object data = parse.get("data");
            if (null == data) {
                throw new RuntimeException();
            }
            final Object url = ((JSONArray) data).get(0);
            if (null == url) {
                throw new RuntimeException();
            }
            this.newFileUrl = (String) ((JSONObject) url).get("url");
            return  (String) ((JSONObject) url).get("dfsId");
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
    public List<VirtualFile> fileList(FileListRequesr fileListRequesr){
        return null;
    }

    @Override
    public boolean exists(String fileName) {
        return false;
    }

    public static void main(String[] args) {
        final FileProperties fileProperties = new FileProperties();
        fileProperties.getXmly().setCookies("Hm_lvt_4a7d8ec50cfd6af753c4f8aee3425070=1639114684; login_type=password_mobile; _xmLog=h5&9ea2efdf-7a5a-4f8d-8269-d9934707ef09&2.4.7-alpha.3; xm-page-viewid=ximalaya-web; x_xmly_traffic=utm_source%253A%2526utm_medium%253A%2526utm_campaign%253A%2526utm_content%253A%2526utm_term%253A%2526utm_from%253A; fds_otp=6230401576727727914; 1&remember_me=y; 1&_token=296993092&6B46C2A0340N0CBC4A6620979932BB1AF9474CD57A95BFBAEA25D9FDEE7710FBF352868FED80140M28D23A81E28D8FF_; 1_l_flag=296993092&6B46C2A0340N0CBC4A6620979932BB1AF9474CD57A95BFBAEA25D9FDEE7710FBF352868FED80140M28D23A81E28D8FF__2021-12-1015:49:16; Hm_lpvt_4a7d8ec50cfd6af753c4f8aee3425070=1639122558\n" + "origin: https://www.ximalaya.com");
        final XMLYApiClient init = new XMLYApiClient(fileProperties);
        final VirtualFile virtualFile = init.uploadFile(new File("/Users/Dabao/Downloads/qrcode_for_gh_da74abc7de78_258.jpg"));
        System.out.println(virtualFile);
//        final String s1 = "username=17600004572&password=zhoudabao521.";
//        final String s = HttpUtils.DataPost(requestUrl + "/token", s1);
//        final JSONObject jsonObject = JSONObject.parseObject(s);
    }
}
