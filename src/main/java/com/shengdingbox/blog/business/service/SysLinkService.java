package com.shengdingbox.blog.business.service;


import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.entity.Link;
import com.shengdingbox.blog.business.vo.LinkConditionVO;
import com.shengdingbox.blog.framework.exception.DabaoLinkException;
import com.shengdingbox.blog.framework.object.AbstractService;

/**
 * 友情链接
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface SysLinkService extends AbstractService<Link, Long> {

    Link getOneByUrl(String url);

    PageInfo<Link> findPageBreakByCondition(LinkConditionVO vo);

    /**
     * 查询可在首页显示的友情链接列表
     *
     * @return
     */
    List<Link> listOfIndex();

    /**
     * 查询可在内页显示的友情链接列表
     *
     * @return
     */
    List<Link> listOfInside();

    /**
     * 查询已禁用的友情链接列表
     *
     * @return
     */
    List<Link> listOfDisable();

    /**
     * 分组获取所有连接
     * {index:首页显示,inside:内页,disable:禁用}
     *
     * @return
     */
    Map<String, List<Link>> listAllByGroup();

    /**
     * 自动添加友链
     *
     * @param link
     * @return
     */
    boolean autoLink(Link link) throws DabaoLinkException;
}
