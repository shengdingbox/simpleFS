package com.shengdingbox.blog.persistence.mapper;

import com.shengdingbox.blog.business.vo.ArticleLoveConditionVO;
import com.shengdingbox.blog.persistence.beans.BizArticleLove;
import com.shengdingbox.blog.plugin.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年9月12日
 * @since 1.0
 */
@Repository
public interface BizArticleLoveMapper extends BaseMapper<BizArticleLove>{

    /**
     * 分页查询
     * @param vo
     *
     * @return
     */
    List<BizArticleLove> findPageBreakByCondition(ArticleLoveConditionVO vo);
}
