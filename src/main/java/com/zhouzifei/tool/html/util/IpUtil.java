package com.zhouzifei.tool.html.util;

import com.zhouzifei.tool.html.dto.IpAddr;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 获取开源IP地址
 *
 * @author 周子斐 (17600004572@163.com)
 * @remark 2020/8/7
 * @Description
 */
@Slf4j
public class IpUtil {
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    public static String getIpAddress(HttpServletRequest request) {
        log.info(request.toString());
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * 校验IP
     *
     * @param ip
     * @return
     */
    private static boolean checkIp(String ip) {
        return !StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    public static String randIP() {
        Random random = new Random(System.currentTimeMillis());
        return (random.nextInt(255) + 1) + "." + (random.nextInt(255) + 1)
                + "." + (random.nextInt(255) + 1) + "."
                + (random.nextInt(255) + 1);
    }

    /**
     * 判断设备 是否使用代理上网
     */
    public static IpAddr isProxy(HttpServletRequest request) {
        String proxyAddress = System.getProperty("http.proxyHost");
        String portStr = System.getProperty("http.proxyPort");
        int proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        final IpAddr ipAddress = new IpAddr();
        ipAddress.setProxyHost(proxyAddress);
        ipAddress.setProxyPort(portStr);
        ipAddress.setXForwardedFor(request.getHeader("x-forwarded-for"));
        ipAddress.setHttpForwardedFor(request.getHeader("HTTP-x-forwarded-for"));
        ipAddress.setProxyClientIp(request.getHeader("Proxy-Client-IP"));
        ipAddress.setHttpClientIp(request.getHeader("http-Client-IP"));
        ipAddress.setWlProxyClientIp(request.getHeader("WL-Proxy-Client-IP"));
        ipAddress.setRemoteAddr(request.getRemoteAddr());
        ipAddress.setProxy((!StringUtils.isEmpty(proxyAddress)) && (proxyPort != -1));
        return ipAddress;
    }

    /**
     * 使用代理上网
     */
    public static Map<String, String> addProxy(String proxyHost, String proxyPort, String ip) {
        final Map<String, String> map = new HashMap<>();
        if(null == ip){
            ip = randIP();
        }
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        map.put("x-forwarded-for", ip);
        map.put("HTTP-x-forwarded-for", ip);
        map.put("Proxy-Client-IP", ip);
        map.put("http-Client-IP", ip);
        map.put("WL-Proxy-Client-IP", ip);
        return map;
    }
}
