package com.shengdingbox.blog.business.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.SysTemplate;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class Template {
    private SysTemplate sysTemplate;

    public Template() {
        this.sysTemplate = new SysTemplate();
    }

    public Template(SysTemplate sysTemplate) {
        this.sysTemplate = sysTemplate;
    }

    @JsonIgnore
    public SysTemplate getSysTemplate() {
        return this.sysTemplate;
    }

    public Long getId() {
        return this.sysTemplate.getId();
    }

    public void setId(Long id) {
        this.sysTemplate.setId(id);
    }

    public String getRefKey() {
        return this.sysTemplate.getRefKey();
    }

    public void setRefKey(String refKey) {
        this.sysTemplate.setRefKey(refKey);
    }

    public String getRefValue() {
        return this.sysTemplate.getRefValue();
    }

    public void setRefValue(String refValue) {
        this.sysTemplate.setRefValue(refValue);
    }

    public Date getCreateTime() {
        return this.sysTemplate.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.sysTemplate.setCreateTime(createTime);
    }

    public Date getUpdateTime() {
        return this.sysTemplate.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.sysTemplate.setUpdateTime(updateTime);
    }

}

