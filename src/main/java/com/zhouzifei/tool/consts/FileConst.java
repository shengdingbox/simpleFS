package com.zhouzifei.tool.consts;

/**
 * 文件常量类
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class FileConst {

    /**
     * 默认图片的宽度:为一数组，如果为一个元素则必须等于该值才合格，如果为两个值，则为图片宽度应符合的区间值。<strong>元素个数最多只可为两个</strong>
     */
    public static final int[] DEFAULT_IMG_WIDTH = {0, 1366};

    /**
     * 默认图片的高度
     */
    public static final int[] DEFAULT_IMG_HEIGHT = {0, 768};

    /**
     * 默认图片的大小：单位B, 52428800B = 51200KB = 50M
     */
    public static final int[] DEFAULT_IMG_SIZE = {1024, 52428800};
}
