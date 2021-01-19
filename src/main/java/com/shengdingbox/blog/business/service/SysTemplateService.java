package com.shengdingbox.blog.business.service;


import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.Template;
import com.shengdingbox.blog.business.enums.TemplateKeyEnum;
import com.shengdingbox.blog.business.vo.TemplateConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;


/**
 * 系统模板
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysTemplateService extends AbstractService<Template, Long> {

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    PageInfo<Template> findPageBreakByCondition(TemplateConditionVO vo);

    /**
     * 通过key获取模板信息
     *
     * @param key
     * @return
     */
    Template getTemplate(TemplateKeyEnum key);

    /**
     * 通过key获取模板信息
     *
     * @param key
     * @return
     */
    Template getTemplate(String key);
}
