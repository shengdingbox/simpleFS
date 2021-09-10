package com.zhouzifei.tool.cache.common;

import java.io.IOException;

/**
 * 对象序列化工具包
 *
 * @author tngou@tngou.net
 */
public class SerializationUtils {

    private static Serializer g_ser;
    static {    
           g_ser = new JavaSerializer(); //使用的是 FST 实例化工具      
    }
    public static byte[] serialize(Object obj) throws IOException {
        return g_ser.serialize(obj);
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        return g_ser.deserialize(bytes);
    }

}
