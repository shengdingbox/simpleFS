package com.zhouzifei.tool.media.file.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ftp上传下载工具类
 * <p>Title: FtpUtil</p>
 * <p>Description: </p>
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 */
@Slf4j
public class FtpUtil {
    /**
     * 维护FTPClient实例
     */
    private final static LinkedBlockingQueue<FTPClient> FTP_CLIENT_QUEUE = new LinkedBlockingQueue<>();

    /**
     * 创建目录
     *
     * @param ftpConfig  配置
     * @param remotePath 需要创建目录的目录
     * @param makePath   需要创建的目录
     * @return 是否创建成功
     */
    public static boolean makeDirectory(FtpConfig ftpConfig, String remotePath, String makePath) {
        try {
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            if (!changeResult) {
                throw new RuntimeException("切换目录失败");
            }
            boolean result = ftpClient.makeDirectory(makePath);
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移动文件
     *
     * @param ftpConfig 配置
     * @param fromPath  待移动目录
     * @param fromName  待移动文件名
     * @param toPath    移动后目录
     * @param toName    移动后文件名
     * @return 是否移动成功
     */
    public static boolean moveFile(FtpConfig ftpConfig, String fromPath, String fromName, String toPath, String toName) {
        try {
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean moveResult = ftpClient.changeWorkingDirectory(toPath);
            if (!moveResult) {
                //创建目录
                mkdir(ftpClient, toPath);
                log.error("FtpUtil切换目录失败,正在创建目录{}",toPath);
            }
            boolean changeResult = ftpClient.changeWorkingDirectory(fromPath);
            if (!changeResult) {
                throw new RuntimeException("切换目录失败");
            }
            boolean result = ftpClient.rename(fromName, toPath + File.separator + toName);
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除文件
     *
     * @param ftpConfig  配置
     * @param remotePath 远程目录
     * @param fileName   文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(FtpConfig ftpConfig, String remotePath, String fileName) {
        try {
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            if (!changeResult) {
                throw new RuntimeException("切换目录失败");
            }
            boolean result = ftpClient.deleteFile(fileName);
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     *
     * @param ftpConfig  配置
     * @param remotePath 远程目录
     * @param fileNames  文件名
     * @return 是否删除成功
     */
    public static boolean deleteFiles(FtpConfig ftpConfig, String remotePath, Set<String> fileNames) {
        try {
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            boolean result = false;
            if (!changeResult) {
                throw new RuntimeException("切换目录失败");
            }
            for (String name : fileNames) {
                result = deleteFile(ftpConfig, remotePath, name);
            }
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件
     *
     * @param ftpConfig  配置
     * @param remotePath 远程目录
     * @param fileName   文件名
     * @param localPath  本地目录
     * @param localName  本地文件名
     * @return 是否下载成功
     */
    public static boolean download(FtpConfig ftpConfig, String remotePath, String fileName, String localPath, String localName) {
        try {
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            if (!changeResult) {
                throw new RuntimeException("切换目录失败");
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File file = new File(localPath, localName);
            if (!file.getParentFile().exists()) {
                boolean mkdirsResult = file.getParentFile().mkdirs();
                if (!mkdirsResult) {
                    throw new RuntimeException("创建目录失败");
                }
            }
            if (!file.exists()) {
                boolean createFileResult = file.createNewFile();
                if (!createFileResult) {
                    throw new RuntimeException("创建文件失败");
                }
            }
            OutputStream outputStream = new FileOutputStream(file);
            boolean result = ftpClient.retrieveFile(fileName, outputStream);
            outputStream.flush();
            outputStream.close();
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件
     *
     * @param ftpConfig   配置
     * @param remotePath  远程目录
     * @param inputStream 待上传文件输入流
     * @param fileName    文件名
     * @return 是否上传成功
     */
    public static boolean upload(FtpConfig ftpConfig, String remotePath, InputStream inputStream, String fileName) {
        try {
            log.info("ftpUtil开始连接ftp,当前配置为{}", ftpConfig.toString());
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            if (!changeResult) {
                //创建目录
                mkdir(ftpClient, remotePath);
                log.error("FtpUtil切换目录失败,正在创建目录");
                ftpClient.changeWorkingDirectory(remotePath);
            }
            // 设置被动模式
            ftpClient.enterLocalPassiveMode();
            // 设置流上传方式
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            // 设置二进制上传
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //中文存在问题
            // 上传 fileName为上传后的文件名
            boolean result = ftpClient.storeFile(fileName, inputStream);
            // 关闭本地文件流
            inputStream.close();
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件夹
     *
     * @param ftpConfig  配置
     * @param remotePath 远程目录
     * @param tempDir    待上传文件夹
     * @return 是否上传成功
     */
    public static boolean uploadDir(FtpConfig ftpConfig, String remotePath, String tempDir) {
        try {
            log.info("ftpUtil开始连接ftp,当前配置为{}", ftpConfig.toString());
            FTPClient ftpClient = connectClient(ftpConfig);
            boolean changeResult = ftpClient.changeWorkingDirectory(remotePath);
            if (!changeResult) {
                //创建目录
                mkdir(ftpClient, remotePath);
                log.error("FtpUtil切换目录失败,正在创建目录");
                ftpClient.changeWorkingDirectory(remotePath);
            }
            // 设置被动模式
            ftpClient.enterLocalPassiveMode();
            // 设置流上传方式
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            // 设置二进制上传
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //中文存在问题
            // 上传 fileName为上传后的文件名
            File file = new File(tempDir);
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
            boolean result = false;
            if (files != null) {
                for (File file1 : files) {
                    String fileName = file1.getName();
                    FileInputStream inputStream = new FileInputStream(file1);
                    result = ftpClient.storeFile(fileName, inputStream);
                    // 关闭本地文件流
                    inputStream.close();
                }
            }
            // 退出FTP
            ftpClient.logout();
            //归还对象
            offer(ftpClient);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * @下载文件夹
     * @param tempDir 本地地址
     * @param remotePath 远程文件夹
     * */
    public static boolean downLoadDirectory(FtpConfig ftpConfig, String remotePath, String tempDir) {
        try {
            log.info("ftpUtil开始连接ftp,当前配置为{}", ftpConfig.toString());
            FTPClient ftpClient = connectClient(ftpConfig);
            new File(tempDir).mkdirs();
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] allFile = ftpClient.listFiles(remotePath);
            for (FTPFile ftpFile : allFile) {
                //远端当前文件名称
                String name = ftpFile.getName();
                //当前文件后缀
                String suffix = name.substring(name.lastIndexOf(".") + 1);
                if ("END".equals(suffix)) {
                    //如果时END文件,就去下载对应的txt文件
                    String newName = name.substring(0, name.lastIndexOf("."));
                    if (!ftpFile.isDirectory()) {
                        boolean rename = ftpClient.rename(name, newName + ".OPEN");
                        log.info("修改文件是否成功{}", rename);
                        downloadOn(newName, tempDir, remotePath, ftpClient);
                        break;
                    }
                }
            }
            for (FTPFile ftpFile : allFile) {
                if (ftpFile.isDirectory()) {
                    String remoteDirectoryPath = remotePath + "/" + ftpFile.getName();
                    downLoadDirectory(ftpConfig, tempDir, remoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载文件夹失败");
            return false;
        }
        return true;
    }

    public static boolean downLoadFile(FtpConfig ftpConfig, String remotePath, String tempDir) {
        try {
            log.info("ftpUtil开始连接ftp,当前配置为{}", ftpConfig.toString());
            FTPClient ftpClient = connectClient(ftpConfig);
            new File(tempDir).mkdirs();
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] allFile = ftpClient.listFiles(remotePath);
            for (FTPFile ftpFile : allFile) {
                log.info("ftpFile:{}",ftpClient);
                String name = ftpFile.getName();
                String newName = name.substring(0, name.lastIndexOf("."));
                if (!ftpFile.isDirectory()) {
                    downloadOn(name, tempDir, remotePath.substring(0,remotePath.lastIndexOf("/")), ftpClient);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载文件失败");
            return false;
        }
        return true;
    }

    private static boolean downloadOn(String remoteFileName, String localDires,
                                      String remoteDownLoadPath, FTPClient ftpClient) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    strFilePath));
            log.info(remoteFileName + "开始下载....");
            success = ftpClient.retrieveFile(remoteFileName, outStream);
            if (success) {
                log.info(remoteFileName + "成功下载到" + strFilePath);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!success) {
            log.error(remoteFileName + "下载失败!!!");
        }
        return success;
    }

    private static void mkdir(FTPClient ftpClient, String path) throws IOException {
        String[] split = path.split("/");
        ftpClient.changeWorkingDirectory("/");
        for (String s : split) {
            boolean b = ftpClient.changeWorkingDirectory(s);
            if (!b) {
                ftpClient.makeDirectory(s);
                ftpClient.changeWorkingDirectory(s);
            }
        }
    }

    /**
     * 登录ftp
     *
     * @param ftpConfig 配置
     * @return 是否登录成功
     * @throws IOException
     */
    public static FTPClient connectClient(FtpConfig ftpConfig) throws IOException {
        FTPClient ftpClient = getClient();
        // 连接FTP服务器
        ftpClient.connect(ftpConfig.ip, ftpConfig.port);
        // 登录FTP
        ftpClient.login(ftpConfig.userName, ftpConfig.password);
        // 正常返回230登陆成功
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new RuntimeException("连接ftp失败");
        }
        ftpClient.setControlEncoding("GBK");
        return ftpClient;
    }

    /**
     * 获取ftpClient对象
     *
     * @return 获取client对象
     */
    private static FTPClient getClient() {
        FTPClient ftpClient = FTP_CLIENT_QUEUE.poll();
        if (ftpClient != null) {
            return ftpClient;
        }
        return new FTPClient();
    }

    private static void offer(FTPClient ftpClient) {
        FTP_CLIENT_QUEUE.offer(ftpClient);
    }

    /**
     * 连接ftp配置
     */
    @Data
    @AllArgsConstructor
    public static class FtpConfig {
        private String ip;
        private int port;
        private String userName;
        private String password;
    }
}