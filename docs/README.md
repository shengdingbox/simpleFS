# simpleFS
它是一个小型整合型的工具类，带有整合(阿里云，七牛云，又拍云，腾讯云，华为云，~~百度云~~，本地上传)OSS上传，短信发送(阿里云，腾讯云，七牛云)，文件加工类，它可以让我们脱离繁琐的开发流程，让开发变得**So easy!**。

# 支持平台

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

## 1、安装

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
拉取代码

```shell
git clone https://gitee.com/zifeiZhou/simpleFS.git(国内，版本较为落后)
git clone https://github.com/shengdingbox/simpleFS.git(默认)
```
编译构建
```shell
mvn clean install
```

## 2、配置


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
    domain-url: 七牛云访问地址
    bucket-name: 七牛云空间名称
    region: 七牛云地域
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
- 启动配置类赋值

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


## 开发

- 初始化

```java
@Autowired
private SimpleFsProperties simpleFsProperties;

FileUploader uploader = FileUploader.builder()
        .simpleFsProperties(simpleFsProperties)
        .progressListener(progressListener)
        .domainUrl(domainUrl)
        .accessKey(accessKey)
        .secretKey(secretKey)
        .region(region)
        .bucketName(bucketName)
        .storageType(storageType)
        .build();

ApiClient apiClient = uploader.execute();
```
- 文件上传
```java
VirtualFile virtualFile = apiClient.uploadFile(file, TEST_OBJECT_NAME);

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

- 文件上传最佳实践

```java
package com.zhouzifei.oss;

import com.zhouzifei.tool.config.QiniuFileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.listener.ProgressListener;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.service.FileUploader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;

@SpringBootApplication(scanBasePackages = "com.zhouzifei.*")
@SpringBootTest
@ActiveProfiles("oss")
public class OssTemplateTest {

    @Autowired
    private SimpleFsProperties simpleFsProperties;

    /**
     * 测试用文件名,该文件在测试资源文件夹下
     */
    private static final String TEST_OBJECT_NAME = "test.txt";

    @Test
    @SneakyThrows
    public void test() {
        System.out.println(simpleFsProperties);
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void start(String s) {
                System.out.println("开始上传");
            }

            @Override
            public void process(int i, int i1) {
                System.out.println("i=" + i);
                System.out.println("i1=" + i1);
            }

            @Override
            public void end(VirtualFile virtualFile) {
                System.out.println("上传完成");
                System.out.println(virtualFile);

            }
        };
        QiniuFileProperties qiniuFileProperties = simpleFsProperties.getQiniu();
        System.out.println(qiniuFileProperties);

        String domainUrl = qiniuFileProperties.getDomainUrl();
        String accessKey = qiniuFileProperties.getAccessKey();
        String secretKey = qiniuFileProperties.getSecretKey();
        String region = qiniuFileProperties.getRegion();
        String bucketName = qiniuFileProperties.getBucketName();
        String storageType = StorageTypeConst.QINIUYUN.getStorageType();
        
        FileUploader uploader = FileUploader.builder()
                .simpleFsProperties(simpleFsProperties)
                .progressListener(progressListener)
                .domainUrl(domainUrl)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .region(region)
                .bucketName(bucketName)
                .storageType(storageType)
                .build();

        ApiClient apiClient = uploader.execute();


        FileInputStream file = new FileInputStream(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + TEST_OBJECT_NAME));

        VirtualFile virtualFile = apiClient.uploadFile(file, TEST_OBJECT_NAME);
        System.out.println(virtualFile.getFullFilePath());
        System.out.println(virtualFile.getFileHash());


    }

}

```



