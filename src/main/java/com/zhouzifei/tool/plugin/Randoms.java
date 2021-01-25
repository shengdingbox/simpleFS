package com.zhouzifei.tool.plugin;

import java.util.Random;

/**
 * 随机工具类
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
public class Randoms {
    //定义验证码字符.去除了O和I等容易混淆的字母
    private static final char ALPHA[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final Random RANDOM = new Random();

    /**
     * 产生两个数之间的随机数
     *
     * @param min
     *         小数
     * @param max
     *         比min大的数
     * @return int 随机数字
     */
    public static int num(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    /**
     * 产生0--num的随机数,不包括num
     *
     * @param num
     *         数字
     * @return int 随机数字
     */
    public static int num(int num) {
        return RANDOM.nextInt(num);
    }

    public static char alpha() {
        return ALPHA[num(0, ALPHA.length)];
    }
}