package com.zhouzifei.tool.util;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.exception.GlobalFileException;
import org.springframework.web.multipart.MultipartFile;

/**
 * 操作图片工具类
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
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
                    .setSuffix(FileUtil.getSuffix(multipartFile.getOriginalFilename()));
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
            return new VirtualFile()
                    .setWidth(bi.getWidth(null))
                    .setHeight(bi.getHeight(null))
                    .setSize(inputStream.available());
        } catch (Exception e) {
            throw new GlobalFileException("获取图片信息发生异常！", e);
        }
    }
}
