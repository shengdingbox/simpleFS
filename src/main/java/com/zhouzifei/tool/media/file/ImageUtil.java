package com.zhouzifei.tool.media.file;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.GlobalFileException;
import org.springframework.web.multipart.MultipartFile;

/**
 * 操作图片工具类
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
public class ImageUtil {

    /**
     * 获取图片信息
     *
     * @param file
     * @throws IOException
     */
    public static VirtualFile getInfo(File file) {
        if (null == file) {
            return new VirtualFile();
        }
        try {
            return getInfo(new FileInputStream(file))
                    .setSize(file.length())
                    .setOriginalFileName(file.getName())
                    .setSuffix(FileUtil.getSuffix(file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalFileException("获取图片信息发生异常！", e);
        }
    }

    /**
     * 获取图片信息
     *
     * @param multipartFile
     * @throws IOException
     */
    public static VirtualFile getInfo(MultipartFile multipartFile) {
        if (null == multipartFile) {
            return new VirtualFile();
        }
        try {
            return getInfo(multipartFile.getInputStream())
                    .setSize(multipartFile.getSize())
                    .setOriginalFileName(multipartFile.getOriginalFilename())
                    .setSuffix(FileUtil.getSuffix(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalFileException("获取图片信息发生异常！", e);
        }
    }

    /**
     * 获取图片信息
     *
     * @param inputStream
     * @throws IOException
     */
    public static VirtualFile getInfo(InputStream inputStream) {
        try (BufferedInputStream in = new BufferedInputStream(inputStream)) {
            //字节流转图片对象
            Image bi = ImageIO.read(in);
            if (null == bi) {
                return new VirtualFile();
            }
            //获取默认图像的高度，宽度
            return new VirtualFile().setSize(inputStream.available());
        } catch (Exception e) {
            throw new GlobalFileException("获取图片信息发生异常！", e);
        }
    }
    public static BufferedImage addWatermark(InputStream input,String watermark) throws IOException {
        BufferedImage bufImg = ImageIO.read(input);
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
}
