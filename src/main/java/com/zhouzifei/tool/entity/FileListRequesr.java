package com.zhouzifei.tool.entity;

import lombok.Data;

/**
 * @author 周子斐
 * @date 2022/1/12
 * @Description
 */
@Data
public class FileListRequesr {

    private String prefix;
    //对文件名称进行分组的一个字符。所有名称包含指定的前缀且第一次出现delimiter字符之间的文件作为一组元素（commonPrefixes）。	setDelimiter(
    private String delimiter;
    private String marker;
    //限定此次列举文件的最大个数。默认值为100，最大值为1000。	setMaxKeys(
    Integer size;
    Integer page;
    //请求响应体中文件名称采用的编码方式，目前仅支持URL。
    private String encodingType;
}
