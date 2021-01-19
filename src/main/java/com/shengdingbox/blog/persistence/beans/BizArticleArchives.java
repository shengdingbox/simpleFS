package com.shengdingbox.blog.persistence.beans;

import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章归档
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizArticleArchives {
    @Transient
    Long id;
    @Transient
    private String title;
    @Transient
    private String original;
    @Transient
    private String datetime;
}
