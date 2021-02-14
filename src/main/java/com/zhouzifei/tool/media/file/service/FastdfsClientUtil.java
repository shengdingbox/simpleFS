package com.zhouzifei.tool.media.file.service;

import com.zhouzifei.tool.dto.NameValuePair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author 周子斐
 * @date 2021/2/9
 * @Description
 */
@Slf4j
public class FastdfsClientUtil {


    public FastdfsClientUtil() {
    }

    public String upload(byte[] data, String extName) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = null;
        try {
            NameValuePair[] meta_list = null; // new NameValuePair[0];
            TrackerClient trackerClient = new TrackerClient();
            trackerServer = trackerClient.getTrackerServer();
            if (trackerServer == null) {
                log.error("getConnection return null");
            }
            storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
            String fileid = storageClient1.upload_file1(data, extName, meta_list);
            log.debug("uploaded file <{}>", fileid);
            return fileid;
        } catch (Exception ex) {
            log.error("Upload fail", ex);
            return null;
        } finally {
            storageClient1 = null;
        }
    }

    public int delete(String fileId) {
//        System.out.println("deleting ....");
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        StorageClient1 storageClient = null;
        int index = fileId.indexOf('/');
        String groupName = fileId.substring(0, index);
        try {
            final TrackerClient trackerClient = new TrackerClient();
            trackerServer = trackerClient.getTrackerServer();
            if (trackerServer == null) {
                log.error("getConnection return null");
            }
            storageServer = trackerClient.getStoreStorage(trackerServer, groupName);
            storageClient = new StorageClient1(trackerServer, storageServer);
            int result = storageClient.delete_file1(fileId);
            return result;
        } catch (Exception ex) {
            log.error("Delete fail", ex);
            return 1;
        } finally {
            storageClient = null;
        }
    }
}
