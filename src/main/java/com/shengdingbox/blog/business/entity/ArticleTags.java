package com.shengdingbox.blog.business.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.BizArticleTags;


/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class ArticleTags {
    private static final long serialVersionUID = 1L;
    private BizArticleTags bizArticleTags;

    public ArticleTags() {
        this.bizArticleTags = new BizArticleTags();
    }

    public ArticleTags(BizArticleTags bizArticleTags) {
        this.bizArticleTags = bizArticleTags;
    }

    @JsonIgnore
    public BizArticleTags getBizArticleTags() {
        return this.bizArticleTags;
    }

    public Long getId() {
        return this.bizArticleTags.getId();
    }

    public void setId(Long id) {
        this.bizArticleTags.setId(id);
    }

    public long getTagId() {
        return this.bizArticleTags.getTagId();
    }

    public void setTagId(long tagId) {
        this.bizArticleTags.setTagId(tagId);
    }

    public long getArticleId() {
        return this.bizArticleTags.getArticleId();
    }

    public void setArticleId(long articleId) {
        this.bizArticleTags.setArticleId(articleId);
    }

    public Date getCreateTime() {
        return this.bizArticleTags.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.bizArticleTags.setCreateTime(createTime);
    }

    public Date getUpdateTime() {
        return this.bizArticleTags.getUpdateTime();
    }

    public void setUpdateTime(Date updateTime) {
        this.bizArticleTags.setUpdateTime(updateTime);
    }

}

