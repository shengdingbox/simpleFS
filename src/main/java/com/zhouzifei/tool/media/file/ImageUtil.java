package com.zhouzifei.tool.media.file;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * 操作图片工具类
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class ImageUtil {


    public static BufferedImage addWatermark(InputStream input,String watermark) throws IOException {
        BufferedImage bufImg = cn.hutool.core.util.ImageUtil.read(input);
        int height = bufImg.getHeight();
        int width = bufImg.getWidth();
        Graphics2D graphics = bufImg.createGraphics();
        Font font = new Font("宋体", 0, 60);
        graphics.setColor(Color.CYAN);
        graphics.setFont(font);
        int i = graphics.getFontMetrics(graphics.getFont()).charsWidth(watermark.toCharArray(), 0, watermark.length());
        int x = width - i - 10;
        int y = height - 10;
        graphics.drawString(watermark, x, y);
        graphics.dispose();
        return bufImg;
    }

    public static void main(String[] args) throws IOException {
        final InputStream referer = FileUtil.getInputStreamByUrl("http://oss.toupiao518.com/qiniu/vycqrhm2g3ryix7x.gif", "referer");
        final InputStream clone = StreamUtil.clone(referer);
        assert clone != null;
        System.out.println(referer.available());
        System.out.println(FileUtil.getInfo(referer));
    }
}
