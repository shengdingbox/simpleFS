package com.zhouzifei.tool.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * bootstrap table用到的返回json格式
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @note https://www.zhouzifei.com
 * @remark 2019年7月16日
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageResult {
    private Long total;
    private List rows;

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public PageResult() {
    }
}
