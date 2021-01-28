# 什么是 commonTool？
commonTool，如你所见，它是一个小型整合型的工具类，带有整合(阿里云,七牛云,本地)OSS上传,短信发送(阿里云,腾讯云,七牛云),文件加工类,，它可以让我们脱离繁琐的开发流程，让开发变得**So easy!**

### 快速开始

- 引入依赖
```xml
<dependency>
  <groupId>com.zhouzifei</groupId>
  <artifactId>commonTool</artifactId>
  <version>1.0.1</version>
</dependency>
```

## 功能介绍
### 文件上传oss(支持阿里云,七牛云,又拍云,本地上传)
- `application.yml`配置OSS信息
```yml
tool:
  oss:
    storage-type-const: 存储类型(枚举可选择)
    aliyun-access-key: 阿里云ak
    aliyun-access-key-secret: 阿里云sk
    aliyun-bucket-name: 阿里云空间名称
    aliyun-endpoint: 阿里云地域
    aliyun-file-url: 阿里云外网地址
    qiniu-bucket-name: 七牛云空间名称
    qiniu-access-key: 七牛云AK
    qiniu-base-path: 七牛云外网地址
    qiniu-secret-key: 七牛云sk
    local-file-path: 本地路径
    local-file-url: 本地外网地址
    path-prefix: 图片文件夹
```
### m3u8下载类
```java
public class M3u8DownloadUtil {
    public static void main(String[] args) {
        M3u8DTO m3u8Download = M3u8DTO.builder()
                .m3u8Url("下载地址")
                .fileName("下载完的文件名,不带后缀")
                .filePath("下载后的地址")
                .retryCount("重试次数")
                .threadCount("线程数")
                .timeout("超时时间").build();
        M3u8DownloadFactory.M3u8Download instance = M3u8DownloadFactory.getInstance(m3u8Download);
        instance.runDownloadTask();//开始下载
        M3u8DownloadFactory.destroy();//销毁实例
    }
}
```

## 开源推荐
- `spring-boot-demo` 深度学习并实战 spring boot 的项目: [https://github.com/xkcoding/spring-boot-demo](https://github.com/xkcoding/spring-boot-demo)

## 在线文档
- <a href = "http://zifeizhou.gitee.io/commontool/">http://zifeizhou.gitee.io/commontool/</a>
