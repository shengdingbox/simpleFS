package com.zhouzifei.tool.media.file.service;


import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.config.properties.FileProperties;
import com.zhouzifei.tool.dto.M3u8DTO;
import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.media.file.util.MediaFormat;
import com.zhouzifei.tool.util.StringUtils;
import com.zhouzifei.tool.util.ThreadManager;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.concurrent.*;

/**
 * M3u8视频下载工厂
 *
 * @author 周子斐 (17600004572@163.com)
 * @date 2020/3/8
 */
@Slf4j
public class M3u8DownloadFactory {

    private static M3u8Download m3u8Download;

    //默认文件每次读取字节数
    public static final int BYTE_COUNT = 40960;
    //因子
    public static final float FACTOR = 1.15F;

    /**
     * 解决java不支持AES/CBC/PKCS7Padding模式解密
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 获取m3u8Download实例
     *
     * @param m3u8DTO 要下载的实例
     * @return 返回m3u8下载实例
     */
    public static M3u8Download getInstance(M3u8DTO m3u8DTO) {
        if (m3u8Download == null) {
            synchronized (M3u8Download.class) {
                if (m3u8Download == null) {
                    m3u8Download = new M3u8Download(m3u8DTO.getM3u8Url());

                }
            }
        } else {
            m3u8Download.setDownloadUrl(m3u8DTO.getM3u8Url());
        }
        //设置生成目录
        m3u8Download.setDir(m3u8DTO.getFilePath());
        //设置视频名称
        m3u8Download.setFileName(m3u8DTO.getFileName());
        //设置线程数
        m3u8Download.setThreadCount(Integer.parseInt(m3u8DTO.getThreadCount()));
        //设置重试次数
        m3u8Download.setRetryCount(Integer.parseInt(m3u8DTO.getRetryCount()));
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(Long.parseLong(m3u8DTO.getTimeout()) * 1000);
        //设置监听器间隔（单位：毫秒）
        m3u8Download.setInterval(1000L);
        return m3u8Download;
    }

    /**
     * 销毁m3u8Download实例
     */
    public static void destroy() {
        m3u8Download = null;
    }

    /**
     * M3u8视频下载工具类
     */
    public static class M3u8Download {
        //要下载的m3u8链接
        private String downloadUrl;

        //优化内存占用
        private static final BlockingQueue<byte[]> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

        //线程数
        private int threadCount = 1;

        //重试次数
        private int retryCount = 30;

        //链接连接超时时间（单位：毫秒）
        private long timeoutMillisecond = 1000L;

        //合并后的文件存储目录
        private String dir;

        //合并前的ts片段临时存储目录
        private String tempDir;

        //合并后的视频文件名称
        private String fileName;

        //已完成ts片段个数
        private int finishedCount = 0;

        //解密算法名称
        private String method;

        //密钥
        private String key = "";

        //密钥字节
        private byte[] keyBytes = new byte[16];

        //key是否为字节
        private boolean isByte = false;

        //IV
        private String iv = "";

        //所有ts片段下载链接
        private Set<String> tsSet = new LinkedHashSet<>();

        //所有ts片段下载链接
        private final List<String> tsSetAll = new CopyOnWriteArrayList<>();

        //所有ts片段数量
        private int totalCount;

        //解密后的片段
        private final Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(
                o -> Integer.parseInt(o.getName().replace(".xyz", ""))));

        //已经下载的文件大小
        private BigDecimal downloadBytes = new BigDecimal(0);

        //监听间隔
        private volatile long interval = 0L;

        //当前步骤 1：解析视频地址 2：下载视频 3：下载完成，正在合并视频 4：结束
        private int step = 1;

        //云存储配置文件
        FileProperties fileProperties;

        /**
         * 开始执行任务
         */
        public void runDownloadTask() {
            //校验字段
            if (!checkFields()) {
                return;
            }
            setThreadCount(30);
            tempDir = dir + "temp";
            finishedCount = 0;
            method = "";
            key = "";
            isByte = false;
            iv = "";
            tsSet.clear();
            finishedFiles.clear();
            downloadBytes = new BigDecimal(0);
            ThreadManager.getThreadPollProxy().execute(() -> {
                while (step == 1&&null!=m3u8Download) {
                    try {
                        Thread.sleep(1000);
                        log.info("正在解析视频地址，请稍后...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            ThreadManager.getThreadPollProxy().execute(() -> {
                String tsUrl = getTsUrl();
                if (StringUtils.isEmpty(tsUrl)) {
                    log.info("不需要解密");
                }
                startDownload();
            });
        }

        public String runM3u8ToCloudTask(FileProperties fileProperties) {
            //校验字段
            if (!checkFields()) {
                return null;
            }
            if (null == this.fileProperties) {
                this.fileProperties = fileProperties;
            }
            setThreadCount(30);
            //监视视频地址是否解析完成
            ThreadManager.getThreadPollProxy().execute(() -> {
                while (step == 1) {
                    try {
                        Thread.sleep(1000);
                        log.info("正在解析视频地址，请稍后...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            ThreadManager.getThreadPollProxy().execute(() -> {
                String tsUrl = getTsUrl();
                if (StringUtils.isEmpty(tsUrl)) {
                    log.info("不需要解密");
                }
                runDownloadTask();
            });
            return downloadUrl;
        }

        private void saveM3u8() {
            FileUploader uploader = new FileUploader();
            ApiClient apiClient = uploader.getApiClient(fileProperties);
            final StringBuilder stringBuffer = new StringBuilder();
            for (String s : tsSetAll) {
                stringBuffer.append(s);
                stringBuffer.append("\n");
            }
            //log.info(stringBuffer.toString());
            InputStream is = new ByteArrayInputStream(stringBuffer.toString().getBytes());
            try {
                final VirtualFile virtualFile = apiClient.uploadFile(is, "index.m3u8");
                log.info("上传地址为{}", virtualFile);
                downloadUrl = virtualFile.getFullFilePath();
            } catch (Exception e) {
                log.info("index.m3u8{}", stringBuffer.toString());
            }

        }
        private void uploadListener(CountDownLatch latch) {
            new Thread(() -> {
                log.info("检测到{}个视频片段，开始下载！", totalCount);
                log.info("0% (0/{})", totalCount);
                //轮询是否下载成功
                try {
                    while (latch.getCount() > 1) {
                        Thread.sleep(interval);
                        float percent = new BigDecimal(finishedCount).divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        log.info("已下载" + finishedCount + "个\t一共" + totalCount + "个\t已完成" + percent + "%");
                        log.info(percent + "% (" + finishedCount + "/" + totalCount + ")");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("下载完成，正在合并文件！共" + totalCount + "个！" + StringUtils.convertToDownloadSpeed(downloadBytes, 3));
                //开始合并视频
                saveM3u8();
                this.step = 4;
                log.info("视频上传完成，欢迎使用");
            }).start();
        }

        /**
         * 下载视频
         */
        private void startDownload() {
            this.step = 2;
            //线程池
            final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
            int i = 0;
            //如果生成目录不存在，则创建
            File file1 = new File(tempDir);
            if (!file1.exists()) {
                file1.mkdirs();
            }

            totalCount = tsSet.size();
            //执行多线程下载
            for (String s : tsSet) {
                i++;
                fixedThreadPool.execute(getThread(s, i));
            }
            fixedThreadPool.shutdown();
            startListener(fixedThreadPool);
        }

        private void startListener(ExecutorService fixedThreadPool) {
            new Thread(() -> {
                log.info("检测到" + totalCount + "个视频片段，开始下载！");
                log.info("0% (0/" + totalCount + ")");
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        Thread.sleep(interval);
                        float percent = new BigDecimal(finishedCount).divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        log.info("已下载" + finishedCount + "个\t一共" + totalCount + "个\t已完成" + percent + "%");
                        log.info(percent + "% (" + finishedCount + "/" + totalCount + ")");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("下载完成，正在合并文件！共" + finishedFiles.size() + "个！" + StringUtils.convertToDownloadSpeed(downloadBytes, 3));
                //开始合并视频
                mergeTs();
                //删除多余的ts片段
                deleteFiles();
                this.step = 4;
                log.info("视频合并完成，欢迎使用");
            }).start();

            new Thread(() -> {
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                        Thread.sleep(1000L);
                        log.info("下载速度：" + StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 2) + "/s");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        /**
         * 合并下载好的ts片段
         */
        private void mergeTs() {
            this.step = 3;
            try {
                File file = new File(dir + fileName + ".mp4");
                System.gc();
                if (file.exists()) {
                    file.delete();
                } else {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[4096];
                for (File f : finishedFiles) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    int len;
                    while ((len = fileInputStream.read(b)) != -1) {
                        fileOutputStream.write(b, 0, len);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 删除下载好的片段
         */
        private void deleteFiles() {
            File file = new File(tempDir);
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".ts") || f.getName().endsWith(".xyz")) {
                    f.delete();
                }
            }
            file.delete();
        }

        /**
         * 开启下载线程
         *
         * @param urls ts片段链接
         * @param i    ts片段序号
         * @return 线程
         */
        private Thread getThread(String urls, int i) {
            return new Thread(() -> {
                int count = 1;
                HttpURLConnection httpURLConnection = null;
                //xy为未解密的ts片段，如果存在，则删除
                File file2 = new File(tempDir + "/" + i + ".ts");
                if (file2.exists()) {
                    file2.delete();
                }

                OutputStream outputStream = null;
                InputStream inputStream1 = null;
                FileOutputStream outputStream1 = null;
                byte[] bytes;
                try {
                    bytes = BLOCKING_QUEUE.take();
                } catch (InterruptedException e) {
                    bytes = new byte[BYTE_COUNT];
                }
                //重试次数判断
                while (count <= retryCount) {
                    try {
                        //模拟http请求获取ts片段文件
                        URL url = new URL(urls);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                        httpURLConnection.setDoInput(true);
                        InputStream inputStream = httpURLConnection.getInputStream();
                        try {
                            outputStream = new FileOutputStream(file2);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                        int len;
                        //将未解密的ts片段写入文件
                        while ((len = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                            synchronized (this) {
                                downloadBytes = downloadBytes.add(new BigDecimal(len));
                            }
                        }
                        outputStream.flush();
                        inputStream.close();
                        inputStream1 = new FileInputStream(file2);
                        int available = inputStream1.available();
                        if (bytes.length < available) {
                            bytes = new byte[available];
                        }
                        inputStream1.read(bytes);
                        File file = new File(tempDir + "/" + i + ".xyz");
                        outputStream1 = new FileOutputStream(file);
                        //开始解密ts片段，这里我们把ts后缀改为了xyz，改不改都一样
                        byte[] decrypt = decrypt(bytes, available, key, iv, method);
                        if (decrypt == null) {
                            outputStream1.write(bytes, 0, available);
                        } else {
                            outputStream1.write(decrypt);
                        }
                        finishedFiles.add(file);
                        break;
                    } catch (Exception e) {
                        if (e instanceof InvalidKeyException || e instanceof InvalidAlgorithmParameterException) {
                            log.error("解密失败！");
                            break;
                        }
                        log.debug("第" + count + "获取链接重试！\t" + urls);
                        count++;
                    } finally {
                        try {
                            if (inputStream1 != null) {
                                inputStream1.close();
                            }
                            if (outputStream1 != null) {
                                outputStream1.close();
                            }
                            if (outputStream != null) {
                                outputStream.close();
                            }
                            BLOCKING_QUEUE.put(bytes);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
                if (count > retryCount) {
                    throw new ServiceException("连接超时！");
                }
                finishedCount++;
            });
        }

        /**
         * 获取所有的ts片段下载链接
         *
         * @return 链接是否被加密，null为非加密
         */
        private String getTsUrl() {
            StringBuilder content = getUrlContent(downloadUrl, false);
            //判断是否是m3u8链接
            if (!content.toString().contains("#EXTM3U")) {
                throw new ServiceException(downloadUrl + "不是m3u8链接！");
            }

            String[] split = content.toString().split("\\n");
            String keyUrl = "";
            boolean isKey = false;
            for (String s : split) {
                //如果含有此字段，则说明只有一层m3u8链接
                if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                    isKey = true;
                    keyUrl = downloadUrl;
                    break;
                }
                //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
                if (s.contains(".m3u8")) {
                    if (StringUtils.isUrl(s)) {
                        return s;
                    }
                    String relativeUrl = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1);
                    keyUrl = relativeUrl + s;
                    break;
                }
            }
            if (StringUtils.isEmpty(keyUrl)) {
                throw new ServiceException("未发现key链接！");
            }

            //获取密钥
            String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
            if (StringUtils.isNotEmpty(key1)) {
                key = key1;
            } else {
                key = null;
            }
            return key;
        }

        /**
         * 获取ts解密的密钥，并把ts片段加入set集合
         *
         * @param url     密钥链接，如果无密钥的m3u8，则此字段可为空
         * @param content 内容，如果有密钥，则此字段可以为空
         * @return ts是否需要解密，null为不解密
         */
        private String getKey(String url, StringBuilder content) {
            StringBuilder urlContent;
            if (content == null || StringUtils.isEmpty(content.toString())) {
                urlContent = getUrlContent(url, false);
            } else {
                urlContent = content;
            }
            if (!urlContent.toString().contains("#EXTM3U")) {
                throw new ServiceException(downloadUrl + "不是m3u8链接！");
            }

            String[] split = urlContent.toString().split("\\n");
            for (String s : split) {
                //如果含有此字段，则获取加密算法以及获取密钥的链接
                if (s.contains("EXT-X-KEY")) {
                    String[] split1 = s.split(",");
                    for (String s1 : split1) {
                        if (s1.contains("METHOD")) {
                            method = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("URI")) {
                            key = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("IV")) {
                            iv = s1.split("=", 2)[1];
                        }
                    }
                }
            }
            String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
            //将ts片段链接加入set集合
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains("#EXTINF")) {
                    String s1 = split[i + 1];
                    tsSet.add(StringUtils.isUrl(s1) ? s1 : relativeUrl + s1);
                    tsSetAll.add(StringUtils.isUrl(s1) ? s1 : relativeUrl + s1);
                } else {
                    if (i == split.length - 1) {
                        break;
                    } else if (i == 0) {
                        tsSetAll.add(split[i]);
                    }
                    String s1 = split[i + 1];
                    tsSetAll.add(s1);
                }
            }
            if (!StringUtils.isEmpty(key)) {
                key = key.replace("\"", "");
                return getUrlContent(StringUtils.isUrl(key) ? key : relativeUrl + key, true).toString().replaceAll("\\s+", "");
            }
            return null;
        }

        /**
         * 模拟http请求获取内容
         *
         * @param urls  http链接
         * @param isKey 这个url链接是否用于获取key
         * @return 内容
         */
        private StringBuilder getUrlContent(String urls, boolean isKey) {
            int count = 1;
            HttpURLConnection httpURLConnection = null;
            StringBuilder content = new StringBuilder();
            while (count <= retryCount) {
                try {
                    URL url = new URL(urls);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                    httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                    String line;
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    if (isKey) {
                        byte[] bytes = new byte[128];
                        int len;
                        len = inputStream.read(bytes);
                        isByte = true;
                        if (len == 1 << 4) {
                            keyBytes = Arrays.copyOf(bytes, 16);
                            content.append("isByte");
                        } else {
                            content.append(new String(Arrays.copyOf(bytes, len)));
                        }
                        return content;
                    }
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    log.info("地址解析成功！");
                    break;
                } catch (Exception e) {
                    log.debug("第" + count + "获取链接重试！\t" + urls);
                    count++;
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
            if (count > retryCount) {
                throw new ServiceException("连接超时！");
            }
            return content;
        }

        /**
         * 解密ts
         *
         * @param sSrc   ts文件字节数组
         * @param length
         * @param sKey   密钥
         * @return 解密后的字节数组
         */
        private byte[] decrypt(byte[] sSrc, int length, String sKey, String iv, String method) throws Exception {
            if (StringUtils.isNotEmpty(method) && !method.contains("AES")) {
                throw new ServiceException("未知的算法！");
            }
            // 判断Key是否正确
            if (StringUtils.isEmpty(sKey)) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16 && !isByte) {
                throw new ServiceException("Key长度不是16位！");
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(isByte ? keyBytes : sKey.getBytes(StandardCharsets.UTF_8), "AES");
            byte[] ivByte;
            if (iv.startsWith("0x")) {
                ivByte = StringUtils.hexStringToByteArray(iv.substring(2));
            } else {
                ivByte = iv.getBytes();
            }
            if (ivByte.length != 16) {
                ivByte = new byte[16];
            }
            //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(sSrc, 0, length);
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            if (BLOCKING_QUEUE.size() < threadCount) {
                for (int i = BLOCKING_QUEUE.size(); i < threadCount * FACTOR; i++) {
                    try {
                        BLOCKING_QUEUE.put(new byte[BYTE_COUNT]);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            this.threadCount = threadCount;
        }

        /**
         * 校验字段
         *
         * @return
         */
        public boolean checkFields() {
            if (StringUtils.isBlank(downloadUrl)) {
                log.info("请填写视频地址");
                return false;
            }
            if ("m3u8".compareTo(MediaFormat.getMediaFormat(downloadUrl)) != 0) {
                log.info(downloadUrl + "不是一个完整m3u8链接!");
                return false;
            }
            if (StringUtils.isBlank(dir)) {
                log.info("请选择或输入保存目录！");
                return false;
            }
            //保存目录设置为以“/”结尾
            if (!dir.endsWith("/")) {
                dir = dir + "/";
            }
            if (StringUtils.isBlank(fileName)) {
                log.info("保存文件名不得为空！");
                return false;
            }
            if (StringUtils.isBlank(String.valueOf(threadCount))) {
                log.info("线程数不得为空！");
                return false;
            }
            if (!StringUtils.isMatch("^[1-9]\\d*$", String.valueOf(threadCount))) {
                log.info("线程数必须为正整数！");
                return false;
            }
            if (StringUtils.isBlank(String.valueOf(threadCount))) {
                log.info("重试次数不得为空！");
                return false;
            }
            if (!StringUtils.isMatch("^\\d+$", String.valueOf(threadCount))) {
                log.info("重试次数必须为大于或等于0的整数！");
                return false;
            }
            if (StringUtils.isBlank(String.valueOf(timeoutMillisecond))) {
                log.info("连接超时时间不得为空！");
                return false;
            }
            if (!StringUtils.isMatch("^[1-9]\\d*$", String.valueOf(timeoutMillisecond))) {
                log.info("连接超时时间必须为正整数！");
                return false;
            }
            return true;
        }

        public int getRetryCount() {
            return retryCount;
        }

        private void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        private long getTimeoutMillisecond() {
            return timeoutMillisecond;
        }

        private void setTimeoutMillisecond(long timeoutMillisecond) {
            this.timeoutMillisecond = timeoutMillisecond;
        }

        private String getDir() {
            return dir;
        }

        private void setDir(String dir) {
            this.dir = dir;
        }

        private String getFileName() {
            return fileName;
        }

        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        private int getFinishedCount() {
            return finishedCount;
        }

        private void setInterval(long interval) {
            this.interval = interval;
        }

        private void addListener(DownloadListener downloadListener) {
            //监听事件
        }

        public Set<String> getTsSet() {
            return tsSet;
        }

        public void setTsSet(Set<String> tsSet) {
            this.tsSet = tsSet;
        }

        private M3u8Download(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }

    public static void main(String[] args) {
        M3u8DTO m3u8Download = M3u8DTO.builder()
                .m3u8Url("https://cdn.oss-cn-hangzhou.myqcloud.com.xuetuiguang.cn/m3u8video/shouquan.m3u8")
                .fileName("miaozhun")
                .filePath("/Users/Dabao/mp4")
                .retryCount("3")
                .threadCount("3")
                .timeout("5").build();
        M3u8DownloadFactory.M3u8Download instance = M3u8DownloadFactory.getInstance(m3u8Download);
        final String tsUrl = instance.runM3u8ToCloudTask(new FileProperties());//开始下载
//        final Set<String> tsSet = instance.getTsSet();
//        System.out.println(tsSet);
    }
}