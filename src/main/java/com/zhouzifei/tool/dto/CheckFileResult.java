package com.zhouzifei.tool.dto;

import lombok.Data;

import java.io.Serializable;


//上传前文件检测
@Data
public class CheckFileResult implements Serializable {
    private String fileMd5;
    //0:锁未占用,1:锁占用
    private Integer lock;
    //文件分块数量
    private Integer chunkNum;
    //每块文件大小
    private Integer chunkSize;
    //当前已传输到第几块
    private Integer chunkCurr;
    //当前文件总大小
    private Long totalSize;
    //访问路径
    private  String viewPath;
}

