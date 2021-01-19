package com.shengdingbox.blog.business.service;

import java.io.PrintWriter;

import me.zhyd.hunter.config.HunterConfig;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public interface RemoverService {

    void run(Long typeId, HunterConfig config, PrintWriter writer);

    void stop();

    void crawlSingle(Long typeId, String[] url, boolean convertImg, PrintWriter writer);
}
