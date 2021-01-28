package com.zhouzifei.tool.media.message;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 短信发送工具类
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 */
public class SmsUtil {

    public static ExpiringMap<String, String> map = ExpiringMap.builder()
            .maxSize(100)
            .expiration(60, TimeUnit.SECONDS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();

    /**
     * @param mobile       手机号
     * @param rand         验证码
     * @param accessKeyId  认证ak
     * @param secret       认证sk
     * @param signName     签名名称
     * @param templateCode 模板编号
     * @return
     */
    public static String aliyunSms(String mobile, String rand, String accessKeyId, String secret, String signName, String templateCode) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.GET);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", rand);
        request.putQueryParameter("TemplateParam", jsonObject.toJSONString());
        String smsKey = "SMS_SEND_";
        String passCodes = map.get(smsKey + mobile);
        if (StringUtils.isNotBlank(passCodes)) {
            return "短信已发送,请等待";
        }
        map.put(smsKey + mobile, String.valueOf(rand));
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "短信发送成功,短信有效期为5分钟";
    }
    public static String SendBatchSms() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "<accessKeyId>", "<accessSecret>");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendBatchSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumberJson", "[\"17600004572\"]");
        request.putQueryParameter("SignNameJson", "[\"蜜蜂追剧\"]");
        request.putQueryParameter("TemplateCode", "SMS_192577214");
        request.putQueryParameter("TemplateParamJson", "[{\"name\":\"code\"}]");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
