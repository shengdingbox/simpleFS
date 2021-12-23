package com.zhouzifei.tool.dto;

<<<<<<< HEAD
public class FileResult {
=======
import lombok.Data;

import java.io.Serializable;

@Data
public class FileResult implements Serializable {
>>>>>>> d7b7b4a3260e02bed4eb3fed74d199d8c4b2939d
    //文件名
    private String url;
    //文件md5
    private String md5;
    //文件名称
    private String name;
<<<<<<< HEAD

    //文件大小
    private Long lenght;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLenght() {
        return lenght;
    }

    public void setLenght(Long lenght) {
        this.lenght = lenght;
    }
=======
    //文件大小
    private Long lenght;
>>>>>>> d7b7b4a3260e02bed4eb3fed74d199d8c4b2939d
}
