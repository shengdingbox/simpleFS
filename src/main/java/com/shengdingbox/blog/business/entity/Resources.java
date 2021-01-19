package com.shengdingbox.blog.business.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.business.enums.ResourceTypeEnum;
import com.shengdingbox.blog.persistence.beans.SysResources;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class Resources {
    private SysResources sysResources;

    public Resources() {
        this.sysResources = new SysResources();
    }

    public Resources(SysResources sysResources) {
        this.sysResources = sysResources;
    }

    @JsonIgnore
    public SysResources getSysResources() {
        return this.sysResources;
    }

    public Long getId() {
        return this.sysResources.getId();
    }

    public void setId(Long id) {
        this.sysResources.setId(id);
    }

    public String getName() {
        return this.sysResources.getName();
    }

    public void setName(String name) {
        this.sysResources.setName(name);
    }

    public ResourceTypeEnum getType() {
        return this.sysResources.getType() != null ? ResourceTypeEnum.valueOf(this.sysResources.getType()) : null;
    }

    public void setType(ResourceTypeEnum type) {
        this.sysResources.setType(type.toString());
    }

    public String getUrl() {
        return this.sysResources.getUrl();
    }

    public void setUrl(String url) {
        this.sysResources.setUrl(url);
    }

    public String getPermission() {
        return this.sysResources.getPermission();
    }

    public void setPermission(String permission) {
        this.sysResources.setPermission(permission);
    }

    public Long getParentId() {
        return this.sysResources.getParentId();
    }

    public void setParentId(Long parentId) {
        this.sysResources.setParentId(parentId);
    }

    public Integer getSort() {
        return this.sysResources.getSort();
    }

    public void setSort(Integer sort) {
        this.sysResources.setSort(sort);
    }

    public boolean isAvailable() {
        Boolean value = this.sysResources.getAvailable();
        return value != null ? value : false;
    }

    public void setAvailable(boolean available) {
        this.sysResources.setAvailable(available);
    }

    public Boolean getExternal() {
        Boolean value = this.sysResources.getExternal();
        return null == value ? false : value;
    }

    public void setExternal(Boolean external) {
        this.sysResources.setExternal(external);
    }

    public String getIcon() {
        return this.sysResources.getIcon();
    }

    public void setIcon(String icon) {
        this.sysResources.setIcon(icon);
    }

    public Date getCreateTime() {
        return this.sysResources.getCreateTime();
    }

    public void setCreateTime(Date regTime) {
        this.sysResources.setCreateTime(regTime);
    }

    public Date getUpdateTime() {
        return this.sysResources.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.sysResources.setUpdateTime(updateTime);
    }

    public SysResources getParent() {
        return this.sysResources.getParent();
    }

    public void setParent(SysResources parent) {
        this.sysResources.setParent(parent);
    }

    public List<SysResources> getNodes() {
        return this.sysResources.getNodes();
    }

    public void setNodes(List<SysResources> nodes) {
        this.sysResources.setNodes(nodes);
    }
}

