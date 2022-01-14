package com.zhouzifei.tool.util;

import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.consts.UpLoadConstant;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.media.file.util.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zhouzifei.tool.consts.UpLoadConstant.ZERO_LONG;

/**
 * 文件操作工具类
 *
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Slf4j
public class FileUtil {
    private static final Map<String, AtomicInteger> chunkNumContainer = new ConcurrentHashMap<>();
    /**
     * 删除目录，返回删除的文件数
     *
     * @param rootPath 待删除的目录
     * @param fileNum  已删除的文件个数
     * @return 已删除的文件个数
     */
    public static int deleteFiles(String rootPath, int fileNum) {
        File file = new File(rootPath);
        if (!file.exists()) {
            return -1;
        }
        if (file.isDirectory()) {
            File[] sonFiles = file.listFiles();
            if (sonFiles != null && sonFiles.length > 0) {
                for (File sonFile : sonFiles) {
                    if (sonFile.isDirectory()) {
                        fileNum = deleteFiles(sonFile.getAbsolutePath(), fileNum);
                    } else {
                        sonFile.delete();
                        fileNum++;
                    }
                }
            }
        } else {
            file.delete();
        }
        return fileNum;
    }


    public static String getPrefix(File file) {
        return getPrefix(file.getName());
    }

    public static String getPrefix(String fileName) {
        int idx = fileName.lastIndexOf(".");
        int xie = fileName.lastIndexOf("/");
        idx = idx == -1 ? fileName.length() : idx;
        xie = xie == -1 ? 0 : xie + 1;
        return fileName.substring(xie, idx);
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }

    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        index = -1 == index ? fileName.length() : index;
        return fileName.substring(index);
    }
    public static String getSuffixName(String fileName) {
        int index = fileName.lastIndexOf(".");
        index = -1 == index ? fileName.length() : index;
        final String substring = fileName.substring(index);
        return substring.replace(".", "");
    }
    public static String getSuffixByUrl(String imgUrl) {
        String defaultSuffix = "";
        if (StringUtils.isEmpty(imgUrl)) {
            return defaultSuffix;
        }
        String fileName = imgUrl;
        if (imgUrl.contains("/")) {
            fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
        }
        String fileSuffix = getSuffix(fileName);
        return StringUtils.isEmpty(fileSuffix) ? defaultSuffix : fileSuffix;
    }
    public static String getSuffixNameByUrl(String imgUrl) {
        String defaultSuffix = "";
        if (StringUtils.isEmpty(imgUrl)) {
            return defaultSuffix;
        }
        String fileName = imgUrl;
        if (imgUrl.contains("/")) {
            fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
        }
        String fileSuffix = getSuffixName(fileName);
        return StringUtils.isEmpty(fileSuffix) ? defaultSuffix : fileSuffix;
    }
    public static void mkdirs(String filePath) {
        File file = new File(filePath);
        mkdirs(file);
    }

    public static void mkdirs(File file) {
        if (!file.exists()) {
            if (file.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
            }
        }
    }

    public static void checkFilePath(String realFilePath) {
        if (StringUtils.isEmpty(realFilePath)) {
            return;
        }
        File parentDir = new File(realFilePath).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    //下载视频
    public static void down(String src, String path, String name) {
        try {
            URL url = new URL(src);
            // 2.获取链接
            URLConnection conn = url.openConnection();
            long length = conn.getContentLengthLong();
            // 3.输入流
            InputStream inStream = conn.getInputStream();
            // 3.写入文件
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String suffix = getSuffixByUrl(src);
            String saveFile = path + name + suffix;
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1024];
            int i = 0, j = 0;
            int byteRead;
            while ((byteRead = inStream.read(buffer)) != -1) {
                i++;
                fs.write(buffer, 0, byteRead);
                if (i % 500 == 0) {
                    j++;
                    File file2 = new File(saveFile);
                    //控制输出小数点后的位数
                    DecimalFormat df = new DecimalFormat("#.##");
                    float f = (file2.length() / (float) length) * 100;
                    System.out.print("已下载：" + df.format(f) + "%\t\t");
                    if (j % 5 == 0) {
                        log.info("下载完成");
                    }
                }
            }
            log.info("\n已下载：100.00%");
            inStream.close();
            fs.close();
        } catch (IOException e) {
            e.toString();
        } catch (Exception e) {
            e.toString();
        }
    }

    public static String getName(String filePath) {
        if (null == filePath) {
            return filePath;
        } else {
            int len = filePath.length();
            if (0 == len) {
                return filePath;
            } else {
                if (isFileSeparator(filePath.charAt(len - 1))) {
                    --len;
                }
                int begin = 0;
                for (int i = len - 1; i > -1; --i) {
                    char c = filePath.charAt(i);
                    if (isFileSeparator(c)) {
                        begin = i + 1;
                        break;
                    }
                }
                return filePath.substring(begin, len);
            }
        }
    }

    public static InputStream getInputStreamByUrl(String url, String referer) {
        HttpGet httpGet = new HttpGet(checkUrl(url));
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        if (StringUtils.isNotEmpty(referer)) {
            httpGet.setHeader("referer", referer);
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        InputStream in = null;
        try {
            response = httpclient.execute(httpGet);
            in = response.getEntity().getContent();
            if (response.getStatusLine().getStatusCode() == 200) {
                return in;
            } else {
                log.error("Error. {}", parseInputStream(in));
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return in;
    }

    private static String parseInputStream(InputStream in) throws IOException {
        String result = "";
        StringBuilder content = null;
        if (null != in) {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            content = new StringBuilder();
            String line = "";
            while ((line = r.readLine()) != null) {
                content.append(line);
            }
            result = content.toString();
        }
        return result;
    }

    /**
     * 校验Url，并返回完整的url
     *
     * @param url 待校验的url
     */
    public static String checkUrl(String url) {
        if (!org.apache.commons.lang.StringUtils.isEmpty(url)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }
            return url.startsWith("//") ? "https:" + url : "http://" + url;
        }
        return null;
    }

    public static boolean isFileSeparator(char c) {
        return '/' == c || '\\' == c;
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file);
            out.write(data);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var4) {
                    log.debug("Ignore failure in closing the Closeable", var4);
                }
            }
        }

    }

    private static OutputStream openOutputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file);
    }

    public static String download(String imgUrl, String referer, String localPath) {
        String fileName;
        try (InputStream is = FileUtil.getInputStreamByUrl(imgUrl, referer)) {
            if (null == is) {
                return null;
            }
            fileName = DigestUtils.md5Hex(is);
            try( FileOutputStream fos = new FileOutputStream(fileName);) {
                File file = new File(localPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                int bytesWritten = 0, byteCount = 0;
                byte[] b = new byte[1024];
                while ((byteCount = is.read(b)) != -1) {
                    fos.write(b, bytesWritten, byteCount);
                }
            }
        } catch (IOException e) {
            log.error("Error.", e);
            return null;
        }
        return fileName;
    }

    //下载视频
    public static void down(InputStream ins, String saveFile) {
        try (InputStream inputStream = StreamUtil.clone(ins)) {
            int length = inputStream.available();
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1024];
            int i = 0, j = 0;
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                i++;
                fs.write(buffer, 0, byteRead);
                if (i % 500 == 0) {
                    j++;
                    File file2 = new File(saveFile);
                    //控制输出小数点后的位数
                    DecimalFormat df = new DecimalFormat("#.##");
                    float f = (file2.length() / (float) length) * 100;
                    System.out.print("已下载：" + df.format(f) + "%\t\t");
                    if (j % 5 == 0) {
                        log.info("下载完成");
                    }
                }
            }
            log.info("\n已下载：100.00%");
            ins.close();
            fs.close();
        } catch (Exception e) {
            throw new ServiceException("9999999", "文件下载失败", e);
        }
    }
    /**
     * 添加分片并返回是否全部写入完毕
     *
     * @param md5
     * @param chunks
     * @return
     */
    public static boolean addChunkAndCheckAllDone(String md5, Integer chunks) {
        chunkNumContainer.putIfAbsent(md5, new AtomicInteger());
        int currentChunks = chunkNumContainer.get(md5).incrementAndGet();
        if (currentChunks == chunks) {
            chunkNumContainer.remove(md5);
            return true;
        }
        return false;
    }
    /**
     * 获取输入流写入输出流
     *
     * @param fileInputStream
     * @param outputStream
     * @param size
     * @throws IOException
     */
    public static void writeFileToStream(FileInputStream fileInputStream, OutputStream outputStream, Long size) throws IOException {
        FileChannel fileChannel = fileInputStream.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        fileChannel.transferTo(UpLoadConstant.ZERO_LONG, size, writableByteChannel);
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
        fileChannel.close();
        writableByteChannel.close();
    }

    public static void main(String[] args) {
        final String s = checkUrl("/uploads/2020/05/kkixeafkldy.jpg");
        System.out.println(s);
    }
}
