# 什么是 commonTool？
commonTool，如你所见，它是一个小型整合型的工具类，带有整合(阿里云,七牛云,又拍云,腾讯云,华为云,~~百度云~~,本地上传)OSS上传,短信发送(阿里云,腾讯云,七牛云),文件加工类,，它可以让我们脱离繁琐的开发流程，让开发变得**So easy!**

# 快速开始
-  引入依赖
```xml
<dependency>
  <groupId>com.zhouzifei</groupId>
  <artifactId>commonTool</artifactId>
  <version>1.0.3</version>
</dependency>
```
- 使用maven从源码安装
```shell
mvn clean install
```
## 功能介绍
### 文件上传oss(支持阿里云,七牛云,又拍云,腾讯云,华为云,~~百度云~~,本地上传)
- `application.yml`配置OSS信息
```yaml
tool:
  file:
    storage-type-const: 存储类型(枚举可选择)
    bucket-name: 空间名称
    local-file-path: 本地路径
    path-prefix: 图片文件夹
    domain-url: 图片外网地址
    operator-name: 又拍云账号
    operator-pwd: 又拍云密码
    access-key: 授权AK
    secret-key: 授权SK
    endpoint: 地域
```
- 编写上传类
```java
public class FileUpload {

    @Autowired
    FileProperties fileProperties;

    public static void main(String[] args) {
        BaseFileUploader uploader = new BaseFileUploader();
        File file = new File("本地地址");
        //可以不使用配置文件,自己另外指定属性
        fileProperties.setPathPrefix("前缀");
        ApiClient apiClient = uploader.getApiClient(fileProperties);
        VirtualFile virtualFile = apiClient.uploadImg(file);
        System.out.println("上传完的文件信息为"+virtualFile);
        boolean removeFile = apiClient.removeFile("文件名称(带前缀)");
        System.out.println("文件是否删除"+removeFile);
    }
}
```
- 文件下载
```java

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

## 特别鸣谢
- `spring-boot-demo` 深度学习并实战 spring boot 的项目: [https://github.com/xkcoding/spring-boot-demo](https://github.com/xkcoding/spring-boot-demo) 
- `腾讯云COS`免费空间50G,免费流量10G/月 [https://cloud.tencent.com/product/cos](https://cloud.tencent.com/product/cos)
- `七牛云`免费空间10G,免费流量10G/月,免费GET100万次/月[https://www.qiniu.com/prices](https://www.qiniu.com/prices)
- `又拍云`免费空间10G,免费流量15G/月(非开通就有,需要额外申请又拍云联盟,限时1年）[https://www.upyun.com/league](https://www.upyun.com/league)
- `网易云`免费空间50G,免费流量20G/月（目前发现最慷慨的一家）[https://www.163yun.com/nos/free](https://www.163yun.com/nos/free)
- `青云`免费空间10G,免费流量1G/月,另外注意没有免费请求额度[https://www.qingcloud.com/pricing-standard](https://www.qingcloud.com/pricing-standard)
- `阿里云`免费空间40G,免费流量10G/月 （限定新用户、限时6个月）[https://www.aliyun.com/product/oss](https://www.aliyun.com/product/oss)



## 在线文档
- <a href = "http://zifeizhou.gitee.io/commontool/">http://zifeizhou.gitee.io/commontool/</a>
