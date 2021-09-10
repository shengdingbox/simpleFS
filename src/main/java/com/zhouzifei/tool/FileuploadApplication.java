package com.zhouzifei.tool;

import com.zhouzifei.tool.annotation.EnableCommonWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCommonWeb
public class FileuploadApplication {
    public FileuploadApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(FileuploadApplication.class, args);
    }
}
