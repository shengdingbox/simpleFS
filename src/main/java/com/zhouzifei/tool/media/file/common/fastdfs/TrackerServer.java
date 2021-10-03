/**
 * Copyright (C) 2008 Happy Fish / YuQing
 * <p>
 * FastDFS Java Client may be copied only under the terms of the GNU Lesser
 * General Public License (LGPL).
 * Please visit the FastDFS Home Page https://github.com/happyfish100/fastdfs for more detail.
 */

package com.zhouzifei.tool.media.file.common.fastdfs;



import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.media.file.common.fastdfs.pool.Connection;
import com.zhouzifei.tool.media.file.common.fastdfs.pool.ConnectionFactory;
import com.zhouzifei.tool.media.file.common.fastdfs.pool.ConnectionPool;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Tracker Server Info
 *
 * @author Happy Fish / YuQing
 * @version Version 1.11
 */
public class TrackerServer {
    protected InetSocketAddress inetSockAddr;


    public TrackerServer(InetSocketAddress inetSockAddr) throws IOException {
        this.inetSockAddr = inetSockAddr;
    }

    public Connection getConnection() throws ServiceException, IOException {
        Connection connection;
        if (ClientGlobal.g_connection_pool_enabled) {
            connection = ConnectionPool.getConnection(this.inetSockAddr);
        } else {
            connection = ConnectionFactory.create(this.inetSockAddr);
        }
        return connection;
    }
    /**
     * get the server info
     *
     * @return the server info
     */
    public InetSocketAddress getInetSocketAddress() {
        return this.inetSockAddr;
    }

}
