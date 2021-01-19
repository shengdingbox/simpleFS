package com.shengdingbox.blog.business.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.BizTags;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class Tags {
    private BizTags bizTags;

    public Tags() {
        this.bizTags = new BizTags();
    }

    public Tags(BizTags bizTags) {
        this.bizTags = bizTags;
    }

    @JsonIgnore
    public BizTags getBizTags() {
        return this.bizTags;
    }

    public Long getId() {
        return this.bizTags.getId();
    }

    public void setId(Long id) {
        this.bizTags.setId(id);
    }

    public String getName() {
        return this.bizTags.getName();
    }

    public void setName(String name) {
        this.bizTags.setName(name);
    }

    public String getDescription() {
        return this.bizTags.getDescription();
    }

    public void setDescription(String description) {
        this.bizTags.setDescription(description);
    }

    public Date getCreateTime() {
        return this.bizTags.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.bizTags.setCreateTime(createTime);
    }

    public Date getUpdateTime() {
        return this.bizTags.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.bizTags.setUpdateTime(updateTime);
    }

}

