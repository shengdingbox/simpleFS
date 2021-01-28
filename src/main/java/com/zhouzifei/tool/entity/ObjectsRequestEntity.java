package com.zhouzifei.tool.entity;

import lombok.Data;

/**
 * https://help.aliyun.com/document_detail/32015.html?spm=5176.doc32021.6.665.PqGkRT
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Data
public class ObjectsRequestEntity {

    /**
     * 限定返回的object key必须以prefix作为前缀
     */
    private String prefix;
    /**
     * 设定结果从marker之后按字母排序的第一个开始返回
     */
    private String marker;
    /**
     * 限定此次返回object的最大数，如果不设定，默认为100，max-keys取值不能大于1000
     */
    private Integer maxKeys;
    /**
     * 是一个用于对Object名字进行分组的字符。
     * 所有名字包含指定的前缀且第一次出现delimiter字符之间的object作为一组元素——CommonPrefixes
     */
    private String delimiter;
    /**
     * 请求响应体中Object名称采用的编码方式，目前支持url。
     */
    private String encodingType;
}
