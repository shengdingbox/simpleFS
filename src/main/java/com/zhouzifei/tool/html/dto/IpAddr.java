package com.zhouzifei.tool.html.dto;

import lombok.Data;

/**
 * @author 周子斐
 * @date 2021/2/19
 * @Description
 */
@Data
public class IpAddr {
    public  String proxyPort;
    private String httpForwardedFor;
    private String xForwardedFor;
    private String httpClientIp;
    private String remoteAddr;
    private String proxyClientIp;
    private String wlProxyClientIp;
    private String proxyHost;
    public boolean isProxy = false;
}
