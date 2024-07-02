package com.zhouzifei.tool.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Data
public class MetaDataRequest implements Serializable {
    private Integer chunk;
    private String fileMd5;
    private Integer chunks;
    private Integer chunkSize;
    private Integer chunkCurr;
    private String name;
    private Long size;
}
