package com.shengdingbox.blog.persistence.beans;

import java.util.Date;

import com.shengdingbox.blog.framework.object.AbstractDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizArticleLove extends AbstractDO {
    private Long articleId;
    private Long userId;
    private String userIp;
    private Date loveTime;
}
