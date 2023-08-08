# 	simpleFS初认识

> 如你所见，它是一个小型整合型的工具类，带有整合(阿里云,七牛云,又拍云,腾讯云,华为云,~~百度云~~,本地上传)OSS上传,短信发送(阿里云,腾讯云,七牛云),文件加工类,，它可以让我们脱离繁琐的开发流程，让开发变得**So easy!**.

# 支持站点
-------------------------------

| 站点 | 文件上传 | 分片上传 | 断点续传 | 文件下载 | 文件删除 |
| :--: | :--: | :--: | :--: | :--: | :--: |
| **阿里云OSS**  |✔|✔|✔|✔|✔|
| **FastDfs**   |✔|✖|✖|✔|✔|
| **华为云OBS**  |✔|✖|✖|✔|✔|
| **本地上传**   |✔|✖|✖|✔|✔|
| **腾讯云COS**  |✔|✖|✖|✔|✔|
| **七牛云Kodo** |✔|✖|✖|✔|✔|
| **又拍云USS**     |✔|✖|✖|✔|✔|
| **百度云BOS**  |✔|✖|✖|✔|✔|
| **MinIO**  |✔|✖|✖|✔|✔|
| **AWS S3**  |✔|✖|✖|✔|✔|
| **金山云 KS3**  |✔|✖|✖|✔|✔|
| **美团云 MSS**  |✔|✖|✖|✔|✔|
| **京东云 OSS**  |✔|✖|✖|✔|✔|
| **天翼云 OOS**  |✔|✖|✖|✔|✔|
| **移动云 EOS**  |✔|✖|✖|✔|✔|
| **沃云 OSS**  |✔|✖|✖|✔|✔|
| **网易数帆 NOS**  |✔|✖|✖|✔|✔|
| **Ucloud US3**  |✔|✖|✖|✔|✔|
| **青云 QingStor**  |✔|✖|✖|✔|✔|
| **平安云 OBS**  |✔|✖|✖|✔|✔|
| **首云 OSS**  |✔|✖|✖|✔|✔|
| **IBM COS**  |✔|✖|✖|✔|✔|
| **其它兼容 S3 协议的平台**  |✔|✖|✖|✔|✔|

# 快速开始
## 安装方式

### 依赖包方式引入

```xml
<dependency>
  <groupId>com.zhouzifei</groupId>
  <artifactId>simpleFS</artifactId>
  <version>{latest-version}</version>
</dependency>
```
> **latest-version** 版本为：
> - 稳定版本：![](https://img.shields.io/github/v/release/shengdingbox/simpleFS?style=flat-square)
> - 快照版本：![](https://img.shields.io/maven-metadata/v.svg?label=snapshots&metadataUrl=https://repo1.maven.org/maven2/com/zhouzifei/simpleFS/maven-metadata.xml&style=flat-square)
> > 注意：无法引入可添加中央仓库地址。
>
>
> ```xml
> <repositories>
>     <repository>
>         <id>ossrh-snapshot</id>
>         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
>         <snapshots>
>             <enabled>true</enabled>
>         </snapshots>
>     </repository>
> </repositories>
> ```
>
> 如果你想第一时间获取 simpleFS 的最新快照，可以添加下列代码，每次构建时都检查是否有最新的快照（默认每天检查）。
>
> ```diff
>        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
>         <snapshots>
> +           <updatePolicy>always</updatePolicy>
>             <enabled>true</enabled>
>         </snapshots>
> ```

### 源码安装方式引入

```shell
git clone https://gitee.com/zifeiZhou/simpleFS.git(Gitee)
git clone https://github.com/shengdingbox/simpleFS.git(Github)
mvn clean install
```
## 配置存储配置信息

### 阿里云oss配置

- `application.yml`方式
```yaml
simple-fs:
  local:
    local-file-path: 本地路径
    local-url: 本机访问地址
  oss:
    access-key: 阿里云OSS授权AK
    secret-key: 阿里云OSS授权SK
    endpoint: 阿里云OSS地域
    domain-url: 阿里云OSS访问地址
    bucket-name: 阿里云OSS空间名称
  fast:
    user-name: FASTDFS用户名
    pass-word: FASTDFS密码
    server-url: FASTDFS上传地址
    domain-url: FASTDFS访问地址
  huawei:
    access-key: 华为对象存储授权AK
    secret-key: 华为对象存储授权SK
    endpoint: 华为对象存储地域
    domain-url: 华为对象存储访问地址
    bucket-name: 华为对象存储空间名称
  bos:
    access-key: 百度bos授权AK
    secret-key: 百度bos授权SK
    endpoint: 百度bos地域
    domain-url: 百度bos访问地址
    bucket-name: 百度bos空间名称
  qcloud:
    access-key: 腾讯云cos授权AK
    secret-key: 腾讯云cos授权SK
    endpoint: 腾讯云cos地域
    domain-url: 腾讯云cos访问地址
    bucket-name: 腾讯云cos空间名称
    region: 腾讯云cos地域
  qiniu:
    access-key: 七牛云授权AK
    secret-key: 七牛云授权SK
    endpoint: 七牛云地域
    domain-url: 七牛云访问地址
    bucket-name: 七牛云空间名称
  upai:
    user-name: 又拍云用户名
    pass-word: 又拍云密码
    domain-url: 又拍云访问地址
    bucket-name: 又拍云空间名称
  smms:
    user-name: smms用户名
    token: smms-Token
    pass-word: smms密码
  github:
    repository: github仓库
    user: github用户名
    token: githubToken
  aws:
    access-key: AWS授权AK
    secret-key: AWS授权SK
    endpoint: AWS地域
    bucket-name: AWS空间名称
    region: AWS地域
    domain-url: AWS访问地址
```
- `application.properties`方式
```properties
simple-fs.oss.accessKey=授权AK
simple-fs.oss.secretKey=授权SK
simple-fs.oss.endpoint=地域
simple-fs.oss.domain-url=图片外网地址
simple-fs.oss.bucket-name=空间名称
```
启动配置类赋值

```java
import com.zhouzifei.tool.config.OssFileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Slf4j
@Configurable
public class OssConfig {
    @Autowired
    SimpleFsProperties simpleFsProperties;

    @PostConstruct
    public void init() {
        log.info("项目启动中，赋值存储配置类");
        final OssFileProperties aliYunOss = simpleFsProperties.getAliYunOss();
        aliYunOss.setAccessKey("授权AK");
        aliYunOss.setSecretKey("授权SK");
        aliYunOss.setEndpoint("地域");
        aliYunOss.setDomainUrl("图片外网地址");
        aliYunOss.setBucketName("空间名称");
        log.info("项目启动中，加载配置数据数据完成-->" + simpleFsProperties);
    }
}
```

`

























获取ApiClient对象

```java
    @Autowired
    FileProperties fileProperties;

    BaseFileUploader uploader = new BaseFileUploader();
    ApiClient apiClient = uploader.getApiClient(fileProperties);
```
- 文件上传
```java
VirtualFile uploadFile(MultipartFile file);
VirtualFile uploadFile(File file);
VirtualFile uploadFile(InputStream is, String fileName);
```
- 文件下载
```java
void downloadFile(String file,  String localFile);
```
- 切片上传
```java
VirtualFile multipartUpload(File file);
```
- 文件删除
```java
boolean removeFile(String key);
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
- `FASTDFS`一个开源的轻量级分布式文件系统[https://github.com/happyfish100/fastdfs/wiki/](https://github.com/happyfish100/fastdfs/wiki/)
- `FASTDFS`一个开源的轻量级分布式文件系统[https://github.com/happyfish100/fastdfs/wiki/](https://github.com/happyfish100/fastdfs/wiki/)



## 在线文档
- <a href = "http://docs.zhouzifei.com/">http://docs.zhouzifei.com/</a>
