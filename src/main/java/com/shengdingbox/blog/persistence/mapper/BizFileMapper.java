package com.shengdingbox.blog.persistence.mapper;

import com.shengdingbox.blog.business.vo.FileConditionVO;
import com.shengdingbox.blog.persistence.beans.BizFile;
import com.shengdingbox.blog.plugin.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Repository
public interface BizFileMapper extends BaseMapper<BizFile> {

    List<BizFile> findPageBreakByCondition(FileConditionVO vo);
}
