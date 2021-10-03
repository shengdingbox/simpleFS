package com.zhouzifei.tool.html.verification;

import com.zhouzifei.tool.html.util.Randoms;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStream;

/**
 * 验证码抽象类,暂时不支持中文
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class Captcha extends Randoms {
    /**
     *  字体
     */
    protected Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 28);
    /**
     * // 验证码随机字符长度
     */
    protected int len = 5;
    /**
     * // 验证码显示跨度
     */
    protected int width = 150;
    /**
     * // 验证码显示高度
     */
    protected int height = 40;
    /**
     * // 随机字符串
     */
    private String chars = null;

    /**
     * 生成随机字符数组
     *
     * @return 字符数组
     */
    protected char[] alphas() {
        String alpha = alpha(len);
        char[] chars = alpha.toCharArray();
        this.chars = alpha;
        return chars;
    }
    /**
     * 给定范围获得随机颜色
     *
     * @return Color 随机颜色
     */
    protected Color color(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + num(bc - fc);
        int g = fc + num(bc - fc);
        int b = fc + num(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 验证码输出,抽象方法，由子类实现
     *
     * @param os
     *         输出流
     */
    public abstract void out(OutputStream os);

    /**
     * 获取随机字符串
     *
     * @return string
     */
    public String text() {
        return chars;
    }
}