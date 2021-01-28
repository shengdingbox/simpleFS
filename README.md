# 什么是 commonTool？
commonTool，如你所见，它是一个小型整合型的工具类，它可以让我们脱离繁琐的开发流程，让开发变得**So easy!**

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


## 开源推荐
- `spring-boot-demo` 深度学习并实战 spring boot 的项目: [https://github.com/xkcoding/spring-boot-demo](https://github.com/xkcoding/spring-boot-demo)

## 在线文档
 <a href = "http://zifeizhou.gitee.io/commontool/">http://zifeizhou.gitee.io/commontool/</a>
