package com.shengdingbox.blog.business.service;


import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.UpdateRecorde;
import com.shengdingbox.blog.business.vo.UpdateRecordeConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 更新记录
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysUpdateRecordeService extends AbstractService<UpdateRecorde, Long> {

    PageInfo<UpdateRecorde> findPageBreakByCondition(UpdateRecordeConditionVO vo);
}
