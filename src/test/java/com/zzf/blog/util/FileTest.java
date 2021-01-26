package com.zzf.blog.util;

import com.zhouzifei.tool.consts.FileOSSConfig;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.GlobalFileUploader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

/**
 * @author 周子斐
 * @date 2021/1/26
 * @Description
 */
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class FileTest {

    @InjectMocks
    GlobalFileUploader globalFileUploader = new GlobalFileUploader();
    @Mock
    FileOSSConfig ossConfig;

    @Test
    public void  upload(){
        File file = new File("/Users/Dabao/app/app.log");
        VirtualFile upload = globalFileUploader.upload(file, "qiniu", "qiniu");
        System.out.println(upload);
    }
}
