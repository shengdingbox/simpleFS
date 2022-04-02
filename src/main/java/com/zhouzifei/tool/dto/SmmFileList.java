package com.zhouzifei.tool.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 周子斐
 * @date 2022/3/29
 * @Description
 */
@Data
public class SmmFileList implements Serializable {
    private String width;
    private String height;
    private String filename;
    private String storename;
    private String size;
    private String path;
    private String hash;
    private String created_at;
    private String url;
    private String delete;
    private String page;
}
