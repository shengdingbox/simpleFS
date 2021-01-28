//package com.shengdingbox.blog.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.yingshivip.admin.model.CollectLog;
//import com.yingshivip.admin.model.ParamUrl;
//import com.yingshivip.admin.model.ParseSet;
//import com.yingshivip.admin.repository.CollectLogRepository;
//import com.yingshivip.admin.repository.ParamUrlPagingRepository;
//import com.yingshivip.admin.repository.ParseSetRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.Header;
//import org.apache.http.HttpResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.net.URL;
//import java.net.URLConnection;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author 周子斐
// * yingshivip_admin
// * @remark 2020/10/12
//
// */
//@Slf4j
//@Component
//public class CollectUtils {
//
//    @Autowired
//    RedisUtils redisUtils;
//
//
//    public String collectVideo(String args,String url,String userName,String passWord){
//        String cookie = login(userName, passWord, url);
//        log.info("登陆成功cookie为{}", cookie);
//        return collectVideo(args,cookie,url);
//    }
//    public String collectVideo(String args,String cookie,String macUrl) {
//        StringBuilder result = new StringBuilder();
//        BufferedReader in = null;
//        Object o = redisUtils.get(args);
//        int total = 0;
//        if (o != null) {
//            String o1 = (String) o;
//            total = Integer.parseInt(o1) - 1;
//        }
//        //获取系统参数
//        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
//        String dateTime = dateFormat.format(date);
//        ParamUrl paramUrl = paramUrlPagingRepository.getParamUrl("1", args);
//        String flag = paramUrl.getFlag();
//        if (StringUtils.isBlank(flag)) {
//            flag = "122b22a6de622fa999bfe200854d5213";
//        }
//        log.info("flag为" + flag);
//        //开始保存日志
//        CollectLog collectLog = new CollectLog();
//        collectLog.setUrl(args);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String nowTime = simpleDateFormat.format(new Date());
//        collectLog.setDate(nowTime);
//        collectLog.setToken(flag);
//        CollectLog save = collectLogRepository.save(collectLog);
//        while (true) {
//            try {
//                total++;
//                redisUtils.set(args, String.valueOf(total));
//                redisUtils.set(args + "collectId", String.valueOf(save.getId()));
//                result = new StringBuilder();
//                log.info("开始执行任务--->" + args + "当前时间为" + dateTime + "当前执行页数为:" + total);
//                String urlNameString = macUrl + "/admin/collect/api.html?ac=cj&cjflag=" + flag + "&cjurl=";
//                String param = "&h=24&t=&ids=&wd=&type=1&mid=1&opt=0&filter=0&filter_from=&param=&page=";
//                URL realUrl = new URL(urlNameString + args + param + total);
//                // 打开和URL之间的连接
//                URLConnection connection = realUrl.openConnection();
//                // 设置通用的请求属性
//                connection.setRequestProperty("accept", "*/*");
//                connection.setRequestProperty("connection", "Keep-Alive");
//                connection.setRequestProperty("cookie", cookie);
//                // 建立实际的连接
//                connection.connect();
//                // 定义 BufferedReader输入流来读取URL的响应
//                in = new BufferedReader(new InputStreamReader(
//                        connection.getInputStream()));
//                String line;
//                while ((line = in.readLine()) != null) {
//                    result.append(line);
//                }
//                log.info("延时2秒");
//                Thread.sleep(2000);
//            } catch (Exception e) {
//                log.error("发送GET请求出现异常！" + e);
//                e.printStackTrace();
//            }
//            // 使用finally块来关闭输入流
//            finally {
//                try {
//                    if (in != null) {
//                        in.close();
//                    }
//                } catch (Exception e2) {
//                    e2.printStackTrace();
//                }
//            }
//            log.debug(String.valueOf(result));
//            //截取?之后字符串
//            String num = result.substring(result.indexOf("<strong class=\"green\">"), result.indexOf("</strong>")).substring("<strong class=\"green\">".length());
//            String sum = result.substring(result.indexOf("<span class=\"green\">"), result.indexOf("</span")).substring("<span class=\"green\">".length());
//            log.info("执行任务执行完成===>" + args + "当前时间为" + dateTime + "当前执行页数为:" + num + "总执行页数为:" + sum);
//            if (num.equals(sum)) {
//                redisUtils.del(args);
//                CollectLog collectLog1 = collectLogRepository.findById(save.getId()).orElseThrow(RuntimeException::new);
//                collectLog1.setStatus("0");
//                collectLogRepository.save(collectLog1);
//                return dateTime + "执行任务完成" + args + "共执行页数为" + sum;
//
//            }
//            if (Integer.parseInt(num) > Integer.parseInt(sum)) {
//                redisUtils.del(args);
//                CollectLog collectLog1 = collectLogRepository.findById(save.getId()).orElseThrow(RuntimeException::new);
//                collectLog1.setStatus("0");
//                collectLogRepository.save(collectLog1);
//                return dateTime + "执行任务完成" + args + "共执行页数为" + sum;
//
//            }
//        }
//    }
//
//    public ParseSet getSystemParam() {
//        Object systemParam = redisUtils.get("systemParam");
//        if (systemParam != null) {
//            return JSONObject.parseObject((String) systemParam, ParseSet.class);
//        }
//        ParseSet sysParam = parseSetRepository.getSysParam();
//        redisUtils.set("systemParam", JSONObject.toJSONString(sysParam), 86635);
//        return sysParam;
//    }
//
//
//    public String login(String name, String pwd, String url) {
//        Map<String, String> map = new HashMap<>();
//        map.put("admin_name", name);
//        map.put("admin_pwd", pwd);
//        Map<String, String> hear = new HashMap<>();
//        StringBuilder stringBuffer = new StringBuilder();
//        try {
//            HttpResponse post = HttpUtils.doPost(url
//                    , "/admin/index/login.html"
//                    , "post"
//                    , hear
//                    , null
//                    , map);
//            Header[] allHeaders = post.getAllHeaders();
//            for (Header allHeader : allHeaders) {
//                if ("Set-Cookie".equals(allHeader.getName())) {
//                    stringBuffer.append(allHeader.getValue());
//                    stringBuffer.append(";");
//                }
//            }
//            return stringBuffer.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static String getTrace(Throwable t) {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        t.printStackTrace(writer);
//        StringBuffer buffer = stringWriter.getBuffer();
//        return buffer.toString();
//    }
//
//    public static void main(String[] args) {
//        com.yingshivip.admin.utils.CollectUtils collectUtils = new com.yingshivip.admin.utils.CollectUtils();
//        String login = collectUtils.login("feifei", "feifei", "https://www.周子斐tv.cn/admin1.php");
//        System.out.println(login);
//    }
//}
