package com.zhouzifei.tool.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileResult implements Serializable {
    //文件名
    private String url;
    //文件md5
    private String md5;
    //文件名称
    private String name;
    //文件大小
    private Long lenght;
}
