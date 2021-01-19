package com.shengdingbox.blog.business.service;


import java.util.List;

import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.dto.SysNoticeDTO;
import com.shengdingbox.blog.business.entity.Notice;
import com.shengdingbox.blog.business.vo.NoticeConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 系统通知
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysNoticeService extends AbstractService<Notice, Long> {

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    PageInfo<Notice> findPageBreakByCondition(NoticeConditionVO vo);

    /**
     * 获取已发布的通知列表
     *
     * @return
     */
    List<SysNoticeDTO> listRelease();
}
