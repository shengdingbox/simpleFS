package com.shengdingbox.blog.persistence.beans;

import java.util.Date;

import javax.persistence.Column;

import com.shengdingbox.blog.framework.object.AbstractDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author innodev java team
 * @version 1.0
 * @date 2018/12/14 09:23
 * @since 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizFile extends AbstractDO {
    @Column(name = "`size`")
    public Long size;
    public String suffix;
    public Integer width;
    public Integer height;
    private Long userId;
    private String originalFileName;
    private String filePath;
    private String fullFilePath;
    private String fileHash;
    private String uploadType;
    private Date uploadStartTime;
    private Date uploadEndTime;

    private String storageType;
}
