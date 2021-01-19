package com.shengdingbox.blog.persistence.beans;

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
public class SysLog extends AbstractDO {
	private Long userId;
	private String logLevel;
	private String ip;
	private String content;
	private String params;
	private String type;
	private String ua;
	private String os;
	private String browser;
	private String spiderType;
	private String requestUrl;
	private String referer;
}
