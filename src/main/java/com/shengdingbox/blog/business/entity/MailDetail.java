package com.shengdingbox.blog.business.entity;

import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Data
public class MailDetail {

    /**
     * 邮件标题
     */
    private String subject;
    /**
     * 收件人地址
     */
    private String toMailAddress;
    /**
     * 收件人姓名
     */
    private String toUsername;
    /**
     * 抄送人
     */
    private String[] cc;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 附件列表
     */
    private List<String> filePaths;
    private boolean html = true;

    public MailDetail(String title, String toMailAddress, String toUsername, String content) {
        this.subject = title;
        this.toMailAddress = toMailAddress;
        this.toUsername = toUsername;
        this.content = content;
        this.filePaths = null;
    }

    public boolean isExitFile() {
        return getFilePaths() != null && getFilePaths().size() > 0;
    }

    public String[] getFilePathArr() {
        if (!CollectionUtils.isEmpty(getFilePaths())) {
            return filePaths.toArray(new String[filePaths.size()]);
        }
        return null;
    }

    public String[] getCc() {
        return cc;
    }

    public MailDetail setCc(String[] cc) {
        this.cc = cc;
        return this;
    }

    public MailDetail setCc(String cc) {
        if (!StringUtils.isEmpty(cc)) {
            this.cc = new String[]{cc};
        }
        return this;
    }

    public MailDetail setCc(List<String> cc) {
        if (!CollectionUtils.isEmpty(cc)) {
            this.cc = cc.toArray(new String[cc.size()]);
        }
        return this;
    }
}
