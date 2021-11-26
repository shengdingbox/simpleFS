package com.zhouzifei.tool.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 周子斐 (zhouzf@asp.citic.com)
 * @date 2021/11/15
 * @Description
 * @Copyright 中信网络科技有限公司 Copyright (c)
 */
@Data
public class MetaDataRequest implements Serializable {
    private String chunk;
    private String fileMd5;
    private String chunks;
    private Integer chunkSize;
    private Integer chunkCurr;
    private Integer name;
    private Long size;
}
