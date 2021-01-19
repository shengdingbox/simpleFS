package com.shengdingbox.blog.business.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.BizArticleLook;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class ArticleLook {
    private static final long serialVersionUID = 1L;
    private BizArticleLook bizArticleLook;

    public ArticleLook() {
        this.bizArticleLook = new BizArticleLook();
    }

    public ArticleLook(BizArticleLook bizArticleLook) {
        this.bizArticleLook = bizArticleLook;
    }

    @JsonIgnore
    public BizArticleLook getBizArticleLook() {
        return this.bizArticleLook;
    }

    public Long getId() {
        return this.bizArticleLook.getId();
    }

    public void setId(Long id) {
        this.bizArticleLook.setId(id);
    }

    public long getArticleId() {
        return this.bizArticleLook.getArticleId();
    }

    public void setArticleId(Long articleId) {
        this.bizArticleLook.setArticleId(articleId);
    }

    public Long getUserId() {
        return this.bizArticleLook.getUserId();
    }

    public void setUserId(long userId) {
        this.bizArticleLook.setUserId(userId);
    }

    public String getUserIp() {
        return this.bizArticleLook.getUserIp();
    }

    public void setUserIp(String userIp) {
        this.bizArticleLook.setUserIp(userIp);
    }

    public Date getLookTime() {
        return this.bizArticleLook.getLookTime();
    }

    public void setLookTime(Date lookTime) {
        this.bizArticleLook.setLookTime(lookTime);
    }

    public Date getCreateTime() {
        return this.bizArticleLook.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.bizArticleLook.setCreateTime(createTime);
    }

    public Date getUpdateTime() {
        return this.bizArticleLook.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.bizArticleLook.setUpdateTime(updateTime);
    }

}

