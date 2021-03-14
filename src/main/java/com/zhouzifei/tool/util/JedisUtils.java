package com.zhouzifei.tool.util;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/3/11
 * @Description
 */
@Slf4j
@Component
public class JedisUtils {


    @Autowired
    private JedisPool jedisPool;

    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    public synchronized Jedis getJedis() {
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            log.error("Get jedis error : " + e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return jedis;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 得到Key
     *
     * @param key
     * @return
     */
    public String buildKey(String key) {
        return key;
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        try {
            value = StringUtils.isNullOrEmpty(value) ? "" : value;
            getJedis().set(buildKey(key), value);
        } catch (Exception e) {
            log.error("Set key error : " + e);
        }
    }

    /**
     * 设置 过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     * @param value
     */
    public void setString(String key, int seconds, String value) {
        try {
            value = StringUtils.isNullOrEmpty(value) ? "" : value;
            getJedis().setex(buildKey(key), seconds, value);
        } catch (Exception e) {
            log.error("Set keyex error : " + e);
        }
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public String getString(String key) {
        String bKey = buildKey(key);
        if (getJedis() == null || !getJedis().exists(bKey)) {
            return null;
        }
        return getJedis().get(bKey);
    }

    /**
     * 设置 list
     *
     * @param <T>
     * @param key
     */
    public <T> void setList(String key, List<T> list) {
        try {
            getJedis().set(key.getBytes(), serialize(list));
        } catch (Exception e) {
            log.error("Set key error : " + e);
        }
    }


    /**
     * 设置 map
     *
     * @param <T>
     * @param key
     */
    public <T> void setMap(String key, Map<String, T> map) {
        try {
            getJedis().set(key.getBytes(), serialize(map));
        } catch (Exception e) {
            log.error("Set key error : " + e);
        }
    }

    /**
     * 获取list
     *
     * @param <T>
     * @param key
     * @return list
     */
    public <T> Map<String, T> getMap(String key) {
        String bKey = buildKey(key);
        if (getJedis() == null || !getJedis().exists(key.getBytes())) {
            return null;
        }
        byte[] in = getJedis().get(key.getBytes());
        Map<String, T> map = (Map<String, T>) deserialize(in);
        return map;
    }
    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv=null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            try {
                if(os!=null) {
                    os.close();
                }
                if(bos!=null) {
                    bos.close();
                }
            }catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return rv;
    }
    public static Object deserialize(byte[] in) {
        Object rv=null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if(in != null) {
                bis=new ByteArrayInputStream(in);
                is=new ObjectInputStream(bis);
                rv=is.readObject();
                is.close();
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(is!=null) {
                    is.close();
                }
                if(bis!=null) {
                    bis.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return rv;
    }

    public String get(String key) {
        String bKey = buildKey(key);
        final Jedis jedis = getJedis();
        if (jedis == null || !jedis.exists(key.getBytes())) {
            return null;
        }
        final String get = jedis.get(bKey);
        jedisPool.returnResource(jedis);
        return get;
    }

    public void set(String key, String value, int time) {
        String bKey = buildKey(key);
        final Jedis jedis = getJedis();
        jedis.setex(bKey,time,value);
        returnResource(jedis);
    }
    public void set(String key, String value) {
        String bKey = buildKey(key);
        final Jedis jedis = getJedis();
        jedis.set(bKey,value);
        returnResource(jedis);
    }

    public void del(String key) {
        String bKey = buildKey(key);
        final Jedis jedis = getJedis();
        jedis.del(bKey);
        returnResource(jedis);
    }

    public boolean exists(String key) {
        return getJedis().exists(key.getBytes());
    }

    public void flush() {
        getJedis().flushAll();
    }
    /**
     *
     * 此方法将ArrayList集合直接存储为一个字符串
     *
     * @param key
     *            存储的名字
     * @param list
     *            要存储的集合对象
     * @param activeTime
     *            该对象的有效时间，单位为秒
     */
    public  Boolean setArrayList(String key, List<Object> list,
                                       Integer activeTime) {
        if (list != null && key != null && !"".equals(key)) {
            Jedis jedis = getJedis();
            jedis.set(key, JSONArray.toJSONString(list));
            if (activeTime != null && activeTime > 0) {
                jedis.expire(key, activeTime);
            }
            jedis.close();
            return true;
        }
        return false;
    }
    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的Arraylist集合
     *
     * @param key
     *            存储的名字
     * @param beanClass
     *            要封装成为的javaBean
     * @return List
     */
    public  List<Object> getArraylist(String key, Class beanClass) {
        if (key != null && !"".equals(key) && beanClass != null) {
            Jedis jedis = getJedis();
            JSONArray jsonArray = JSONArray.parseArray(jedis.get(key));
            return new ArrayList<Object>(jsonArray);
        }
        return null;
    }
}