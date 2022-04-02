package com.zhouzifei.tool.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2022/3/29
 * @Description
 */
@Data
public class GithubFileList implements Serializable {
    private String name;
    private String path;
    private String sha;
    private String size;
    private String url;
    private String html_url;
    private String git_url;
    private String download_url;
    private String type;
    private Map<String, String> _links;
}
