package com.zhouzifei.tool.config;

import java.io.InputStream;
import java.util.Properties;


public class ReadProper {

    public static String getResourceValue(String key) {
        try {
            InputStream input = ReadProper.class.getResourceAsStream("/application.properties");
            Properties p = new Properties();
            p.load(input);
            input.close();
            if (!p.containsKey(key)) {
                return null;
            }
            return p.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //读取国际化
    public static String getI18n(String key) {
        try {
            InputStream input = ReadProper.class
                    .getResourceAsStream("/i18n_zh_CN.properties");
            Properties p = new Properties();
            p.load(input);
            input.close();
            if (!p.containsKey(key)) {
                return null;
            }
            return p.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
