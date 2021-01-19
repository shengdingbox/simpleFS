package com.shengdingbox.blog.business.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.BizArticleLove;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class ArticleLove {
    private static final long serialVersionUID = 1L;
    private BizArticleLove bizArticleLove;

    public ArticleLove() {
        this.bizArticleLove = new BizArticleLove();
    }

    public ArticleLove(BizArticleLove bizArticleLove) {
        this.bizArticleLove = bizArticleLove;
    }

    @JsonIgnore
    public BizArticleLove getBizArticleLove() {
        return this.bizArticleLove;
    }

    public Long getId() {
        return this.bizArticleLove.getId();
    }

    public void setId(Long id) {
        this.bizArticleLove.setId(id);
    }

    public long getArticleId() {
        return this.bizArticleLove.getArticleId();
    }

    public void setArticleId(long articleId) {
        this.bizArticleLove.setArticleId(articleId);
    }

    public long getUserId() {
        return this.bizArticleLove.getUserId();
    }

    public void setUserId(long userId) {
        this.bizArticleLove.setUserId(userId);
    }

    public String getUserIp() {
        return this.bizArticleLove.getUserIp();
    }

    public void setUserIp(String userIp) {
        this.bizArticleLove.setUserIp(userIp);
    }

    public Date getLoveTime() {
        return this.bizArticleLove.getLoveTime();
    }

    public void setLoveTime(Date loveTime) {
        this.bizArticleLove.setLoveTime(loveTime);
    }

    public Date getCreateTime() {
        return this.bizArticleLove.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.bizArticleLove.setCreateTime(createTime);
    }

    public Date getUpdateTime() {
        return this.bizArticleLove.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.bizArticleLove.setUpdateTime(updateTime);
    }

}

