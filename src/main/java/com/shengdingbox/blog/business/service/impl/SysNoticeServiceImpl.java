package com.shengdingbox.blog.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.business.dto.SysNoticeDTO;
import com.shengdingbox.blog.business.entity.Notice;
import com.shengdingbox.blog.business.enums.NoticeStatusEnum;
import com.shengdingbox.blog.business.service.SysNoticeService;
import com.shengdingbox.blog.business.vo.NoticeConditionVO;
import com.shengdingbox.blog.persistence.beans.SysNotice;
import com.shengdingbox.blog.persistence.mapper.SysNoticeMapper;
import com.shengdingbox.blog.util.BeanConvertUtil;

/**
 * 系统通知
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Service
public class SysNoticeServiceImpl implements SysNoticeService {

    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    @Override
    public PageInfo<Notice> findPageBreakByCondition(NoticeConditionVO vo) {
        PageHelper.startPage(vo.getPageNumber(), vo.getPageSize());
        List<SysNotice> list = sysNoticeMapper.findPageBreakByCondition(vo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Notice> boList = new ArrayList<>();
        for (SysNotice sysNotice : list) {
            boList.add(new Notice(sysNotice));
        }
        PageInfo bean = new PageInfo<SysNotice>(list);
        bean.setList(boList);
        return bean;
    }

    /**
     * 获取已发布的通知列表
     *
     * @return
     */
    @Override
    public List<SysNoticeDTO> listRelease() {
        NoticeConditionVO vo = new NoticeConditionVO();
        vo.setStatus(NoticeStatusEnum.RELEASE.toString());
        List<SysNotice> list = sysNoticeMapper.findPageBreakByCondition(vo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<SysNoticeDTO> boList = new ArrayList<>();
        for (SysNotice sysNotice : list) {
            boList.add(BeanConvertUtil.doConvert(sysNotice, SysNoticeDTO.class));
        }
        return boList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice insert(Notice entity) {
        Assert.notNull(entity, "Notice不可为空！");
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());
        sysNoticeMapper.insertSelective(entity.getSysNotice());
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByPrimaryKey(Long primaryKey) {
        return sysNoticeMapper.deleteByPrimaryKey(primaryKey) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelective(Notice entity) {
        Assert.notNull(entity, "Notice不可为空！");
        entity.setUpdateTime(new Date());
        return sysNoticeMapper.updateByPrimaryKeySelective(entity.getSysNotice()) > 0;
    }

    @Override
    public Notice getByPrimaryKey(Long primaryKey) {
        Assert.notNull(primaryKey, "PrimaryKey不可为空！");
        SysNotice entity = sysNoticeMapper.selectByPrimaryKey(primaryKey);
        return null == entity ? null : new Notice(entity);
    }
}
