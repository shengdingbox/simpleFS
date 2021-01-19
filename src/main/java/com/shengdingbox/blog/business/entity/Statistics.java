package com.shengdingbox.blog.business.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shengdingbox.blog.persistence.beans.BizStatistics;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class Statistics {

    private BizStatistics bizStatistics;

    public Statistics(BizStatistics bizStatistics) {
        this.bizStatistics = bizStatistics;
    }

    public Statistics() {
    }

    @JsonIgnore
    public BizStatistics getBizStatistics() {
        return bizStatistics;
    }

    public String getName() {
        return this.bizStatistics.getName();
    }

    public void setName(String name) {
        this.bizStatistics.setName(name);
    }

    public Integer getValue() {
        return this.bizStatistics.getValue();
    }

    public void setValue(Integer value) {
        this.bizStatistics.setValue(value);
    }
}
