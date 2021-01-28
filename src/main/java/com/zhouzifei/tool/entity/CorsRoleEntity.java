package com.zhouzifei.tool.entity;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CorsRoleEntity extends AbstractEntity {
    /**
     * 指定允许跨域请求的来源
      */
    private ArrayList<String> allowedOrigin;
    /**
     * // 指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD)
     */
    private ArrayList<String> allowedMethod;
    /**
     * // 控制在OPTIONS预取指令中Access-Control-Request-Headers头中指定的header是否允许
     */
    private ArrayList<String> allowedHeader;
    /**
     * // 指定允许用户从应用程序中访问的响应头
     */
    private ArrayList<String> exposedHeader;
    /**
     * // 指定浏览器对特定资源的预取(OPTIONS)请求返回结果的缓存时间,单位为秒
     */
    private int maxAgeSeconds;

    public CorsRoleEntity(String bucket) {
        super(bucket);
        this.maxAgeSeconds = 300;
    }
}
