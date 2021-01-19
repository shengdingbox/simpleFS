package com.shengdingbox.blog.business.service;


import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.Log;
import com.shengdingbox.blog.business.enums.PlatformEnum;
import com.shengdingbox.blog.business.vo.LogConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysLogService extends AbstractService<Log, Integer> {

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    PageInfo<Log> findPageBreakByCondition(LogConditionVO vo);

    void asyncSaveSystemLog(PlatformEnum platform, String bussinessName);
}
