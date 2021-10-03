package com.zhouzifei.tool.media.file.common.fastdfs.pool;


import com.zhouzifei.tool.common.ServiceException;
import com.zhouzifei.tool.media.file.common.fastdfs.ClientGlobal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionFactory {
    /**
     * create from InetSocketAddress
     *
     * @param socketAddress
     * @return
     * @throws IOException
     */
    public static Connection create(InetSocketAddress socketAddress) throws ServiceException {
        try {
            Socket sock = new Socket();
            sock.setReuseAddress(true);
            sock.setSoTimeout(ClientGlobal.g_network_timeout);
            sock.connect(socketAddress, ClientGlobal.g_connect_timeout);
            return new Connection(sock, socketAddress);
        } catch (Exception e) {
            throw new ServiceException("connect to server " + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort() + " fail, emsg:" + e.getMessage());
        }
    }
}
