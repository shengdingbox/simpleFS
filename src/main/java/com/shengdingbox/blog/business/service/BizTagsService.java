package com.shengdingbox.blog.business.service;


import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.Tags;
import com.shengdingbox.blog.business.vo.TagsConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 标签
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface BizTagsService extends AbstractService<Tags, Long> {

    PageInfo<Tags> findPageBreakByCondition(TagsConditionVO vo);

    Tags getByName(String name);
}
