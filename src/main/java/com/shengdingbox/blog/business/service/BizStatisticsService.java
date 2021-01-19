package com.shengdingbox.blog.business.service;

import java.util.List;

import com.shengdingbox.blog.business.entity.Article;
import com.shengdingbox.blog.business.entity.Statistics;

/**
 * 统计
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface BizStatisticsService {
    /**
     * 获取热门文章
     *
     * @return
     */
    List<Article> listHotArticle(int pageSize);

    /**
     * 获取爬虫统计
     *
     * @return
     */
    List<Statistics> listSpider(int pageSize);

    /**
     * 获取文章分类统计
     *
     * @return
     */
    List<Statistics> listType(int pageSize);

}
