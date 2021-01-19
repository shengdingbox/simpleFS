package com.shengdingbox.blog.business.service;


import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.Role;
import com.shengdingbox.blog.business.vo.RoleConditionVO;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 角色
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysRoleService extends AbstractService<Role, Long> {

    /**
     * 获取ztree使用的角色列表
     *
     * @param uid
     * @return
     */
    List<Map<String, Object>> queryRoleListWithSelected(Integer uid);

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    PageInfo<Role> findPageBreakByCondition(RoleConditionVO vo);

    /**
     * 获取用户的角色
     *
     * @param userId
     * @return
     */
    List<Role> listRolesByUserId(Long userId);
}
