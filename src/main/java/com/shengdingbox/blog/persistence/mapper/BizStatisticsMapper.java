package com.shengdingbox.blog.persistence.mapper;

import com.shengdingbox.blog.persistence.beans.BizStatistics;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 统计
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Repository
public interface BizStatisticsMapper {

    List<BizStatistics> listSpider();

    List<BizStatistics> listType();
}
