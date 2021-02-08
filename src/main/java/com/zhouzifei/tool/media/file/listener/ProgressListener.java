package com.zhouzifei.tool.media.file.listener;

import com.zhouzifei.tool.entity.VirtualFile;

/**
 * @author 周子斐
 * @date 2021/2/3
 * @Description
 */
public interface ProgressListener {

    /**
     * 开始
     * @param msg
     */
    void start(String msg);

    /**
     * 处理中
     * @param finished 目前进度
     * @param sum 总进度
     */
    void process(int finished, int sum);

    /**
     * 结束
     */
    void end(VirtualFile virtualFile);
}
