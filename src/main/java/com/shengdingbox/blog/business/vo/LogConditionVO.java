package com.shengdingbox.blog.business.vo;

import com.shengdingbox.blog.framework.object.BaseConditionVO;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Getter
@Setter
public class LogConditionVO extends BaseConditionVO {
	private Long userId;
	private String logLevel;
	private String type;
	private Boolean spider;
}

