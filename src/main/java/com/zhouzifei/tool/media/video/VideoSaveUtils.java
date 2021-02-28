package com.zhouzifei.tool.media.video;


import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.HttpResponses;
import com.zhouzifei.tool.dto.VideoUrlDTO;
import com.zhouzifei.tool.exception.ServiceException;
import com.zhouzifei.tool.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.data.redis.core.convert.PathIndexResolver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 周子斐
 * parse
 * @remark 2020/7/24

 * @Description
 */
public class VideoSaveUtils {

    /**
     *
     * @param videoUrlDTO 解析原型实体
     * @param fileLocal 保存的本地路径
     * @param fileUrl 返回的域名
     * @return
     */
    public static VideoUrlDTO saveVideo(VideoUrlDTO videoUrlDTO, String fileLocal, String fileUrl) {
        String domain = null;
        if (videoUrlDTO.getDomain() != null) {
            domain = videoUrlDTO.getDomain();
        }
        String parseUrl = videoUrlDTO.getUrl();
        if (StringUtils.isBlank(parseUrl)) {
            return videoUrlDTO;
        }
        if(parseUrl.contains("/")){
            int i = parseUrl.lastIndexOf("/");
            String substring = parseUrl.substring(0, i);
            videoUrlDTO.setPrefixUrl(substring + "/");
        }
        String playUrl = saveLocal(parseUrl, domain, videoUrlDTO, fileLocal,fileUrl);
        videoUrlDTO.setUrl(playUrl);
        return videoUrlDTO;
    }

    /**
     *
     * @param url 视频的原始地址
     * @param domain 请求地址
     * @param videoUrlDTO 返回的视频实体
     * @param fileLocal 本地路径
     * @param fileUrl 返回的域名
     * @return
     */
    public static String saveLocal(String url, String domain, VideoUrlDTO videoUrlDTO, String fileLocal, String fileUrl) {
        String prefixType = videoUrlDTO.getPrefixType();
        String prefixUrl = videoUrlDTO.getPrefixUrl();
        if(domain == null){
            domain = prefixUrl;
        }
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String replace = UUID.randomUUID().toString().replace("-", "");
        if (prefixType.contains(".com")) {
            prefixType = prefixType.replace(".com", "");
        }
        String filePath = "/" + prefixType + "/";
        String fileName = replace + ".m3u8";
        Map<String, String> hear = new HashMap<>();
        hear.put("User-Agent", "Mozilla/4.0 (compatible;MSIE 7.0; Windows NT 5.1; Maxthon;)");
        hear.put("Accept-Encoding", "UTF-8");
        hear.put("referer", domain);
        try {
            HttpResponse get = HttpUtils.doGet(url, "","GET",  hear, new HashMap<>());
            // 设置字符编码
            final long contentLength = get.getEntity().getContentLength();
            if(contentLength > 52428800){
                return url;
            }
            if(contentLength<100){
                //是否为302
                final int statusCode = getStatusCode(url);
                if(200 == statusCode){
                    throw new ServiceException("字节数不够");
                }
            }
            InputStream content = get.getEntity().getContent();
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            //构造一个BufferedReader类来读取文件
            File file2 = new File(fileLocal + filePath);
            File file1 = new File(fileLocal + filePath + fileName);
            if (!file2.isDirectory()) {
                file2.mkdirs();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file1));
            String result;
            InputStreamReader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            prefixUrl = prefixUrl == null ? "" : prefixUrl;
            while ((result = br.readLine()) != null) {
                if (!result.contains("#")) {
                    if (!result.contains("http://")&&!result.contains("https://")) {
                        if (result.contains(VideoTypeConst.M3U8.getType())) {
                            return url;
                        } else {
                            result = prefixUrl + result + "\n";
                        }
                    }else {
                        result = result + "\n";
                    }
                } else {
                    result = result + "\n";
                }
                out.write(result.getBytes());
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
        return fileUrl + filePath + fileName;
    }

    public static int getStatusCode(String url) {
        try {
            HttpURLConnection httpUrlConn = (HttpURLConnection) new URL(
                    url).openConnection();
            // 设置连接主机超时（单位：毫秒）
            httpUrlConn.setConnectTimeout(5000);
            // 设置从主机读取数据超时（单位：毫秒）
            httpUrlConn.setReadTimeout(5000);
            httpUrlConn.setRequestProperty("referer","https://jiexi.dabaotv.cn/");
            System.out.println(new Date());
            return httpUrlConn.getResponseCode();
        } catch (IOException e) {
            return 404;
        }
    }
    public static void main(String[] args) {
//        final VideoUrlDTO videoUrlDTO = new VideoUrlDTO();
//        videoUrlDTO.setPrefixType("aiqiyi.com");
//        saveLocal("https://cache1.parwix.com:4433/m/cb36XQilrkuA30GlehwAzhLtQSvx5CQfMNiJZl3YUbXu908eTs_uL7O5Kua0rqHhf4KhJMEXXcnLGBHWecvp38JRITiwdlAbnRZs82BCytvyU-couayd42oJTVBNIqyVlrlqyGUTTlNPFw2O5LcBQOWWaSdSg6YRMOBntMd78w6BBIZL1hceurpY-x6bBi846yIKhI6vL5pSq_wUn_EIEac3lhwOy5U_wUAM.m3u8"
//        ,""
//        ,videoUrlDTO
//        ,"/Users/Dabao/temp1"
//        ,"");
        final int statusCode = getStatusCode("https://cache1.parwix.com:4433/m/0620ecK6sGZkfExFf0QRX_9O89cuVI2fey82KR6EjGj3cI67Lb-MmDanym4T0LYNVHdBAyI_el64Q3nfsVuceWcddHc2Db9LMTj5ro29aPjBoAryYOeA8gmu81UFNpLS38RNNtHWGMWc1pjiUtycmMcxEm0vW6M-AvhWGPwyIl7yuuX7vZrGOtA_DaL9LbspIFDLX1wrJsawwRQK5mwDOLim1IbPgmA8vg.m3u8");
        System.out.println(statusCode);
    }
}
