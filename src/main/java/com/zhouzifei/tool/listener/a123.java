package com.zhouzifei.tool.listener;

import com.alibaba.fastjson.JSONObject;
import com.zhouzifei.tool.util.RandomsUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.zhouzifei.tool.util.HttpUtils.trustAllCerts;

/**
 * @author 周子斐
 * @date 2022/3/30
 * @Description
 */
public class a123 {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        final String fileName = "IMG_9541.JPG";
        final File file1 = new File("/Users/Dabao/Downloads/IMG_9541.JPG");
        final FileInputStream is = new FileInputStream(file1);
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        // 定义数据分隔线
        String BOUNDARY = "Ju5tH77P15Aw350m3";
        // 服务器的域名
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://picupload.weibo.com/interface/pic_upload.php?&mime=image/jpeg&data=base64&url=0&markpos=1&logo=&nick=0&marks=1&app=miniblog").openConnection();
        // 设置为POST情
        conn.setRequestMethod("POST");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        conn.setSSLSocketFactory(sc.getSocketFactory());
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求头参数
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Cookie", "weibo.com=WBStorage=; SINAGLOBAL=831693548685.6259.1582860680784; UOR=,,login.sina.com.cn; _s_tentry=login.sina.com.cn; Apache=544756383396.53186.1582891591657; login=9249710d0a28c0029265c8b1a2d26525; route=d434df472bb5ab59af1d4577b2b5d916; login_sid_t=ca1be90e898a7dc7280b23edce20666f; cross_origin_proto=SSL; BAYEUX_BROWSER=a100-8hqrtjphin35k97zu3f4vsw; Ugrow-G0=5c7144e56a57a456abed1d1511ad79e8; MCN-G0=5b5bf693a8d1e44e3ac6227524cf3cdf; YF-V5-G0=b588ba2d01e18f0a91ee89335e0afaeb; TC-V5-G0=4de7df00d4dc12eb0897c97413797808; YF-Page-G0=86b4280420ced6d22f1c1e4dc25fe846|1601023551|1601023551; WBtopGlobal_register_version=2020102212; XSRF-TOKEN=7LOrIMXxbj9s0DuwEEIjdV-T; MCN-G0=ad3d8bbbac043ef39edd0800b5622522; SAFEIT-G0=806692d0215763995d725bf01f60132c; SSOLoginState=1645010062; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWTLM2YIwvmxSGuVzGnZMW25JpX5KMhUgL.FoeRS0241hqESKB2dJLoIpHiH8YLxK.LBK2LB.-LxK.L1KMLB--t; SCF=Ap0YeTLdfh7jheQk0NWltIJl5bVSJQxUB5m0VxzSFQD6oAH0cJctFHDFcmBMjsjTGUqiHc50Dma3ad_yzajkJB8.; SUB=_2A25PRp2RDeRhGeVG7FMY-CjOzjiIHXVsNYhZrDV8PUNbmtAKLRWhkW9NT4KMlpqK8-V-MZjBukZk0wO4j0R1UeyV; SRT=D.QqHBJZPA4evp4!Mb4cYGSPSGirSidNsDT!ywTcbHNEYdVruSTE9pMERt4EPKRcsrA4kJ4bWfTsVuObArNrsjVGEIKCsdI4sTT!HsTs9hJGWLiFfn4ZEPSQbd*B.vAflW-P9Rc0lR-yk5DvnJqiQVbiRVPBtS!r3JZPQVqbgVdWiMZ4siOzu4DbmKPWQPmoiJbbHW3Sk5EiGWPSwM-uTOPVpi49ndDPIOdYPSrnlMcyb4bJnJZAoTGEE4rkCJcM1OFyHVmY9NDYSOGYII!oCNqHJA8tkOGYII!noNrsJA8rkWv77; SRF=1648553409; ALF=1680089409; WBPSESS=3erGLKQsbWUwm7fv_RdLQUxuAqnOnw5GIBR4mnUQx1SN84HnGBrpWqKSg0ZqCpvuFrrCj2cjD8cHRDNn_mnm0igjEKb39rg3e_wl_DhtaQ5Bfl9Y7iXEcETGSypEYQh9");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        // 上传文件
        StringBuilder sb = new StringBuilder();
        sb.append(boundaryPrefix);
        sb.append(BOUNDARY);
        sb.append(newLine);
        // 文件参数,photo参数名可以随意修改
        sb.append("Content-Disposition: form-data;name=\"pic1\";filename=\"" + fileName + "\"" + newLine);
        //sb.append("Content-Type:multipart/form-data");
        // 参数头设置完以后需要两个换行，然后才是参数内容
        sb.append(newLine);
        //sb.append(newLine);
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
        String end_data = (boundaryPrefix + BOUNDARY + boundaryPrefix);
        // 写上结尾标识
        out.write(end_data.getBytes("UTF-8"));
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
    }
}
