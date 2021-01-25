package com.zhouzifei.tool.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * Freemarker模板操作工具类
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Slf4j
public class FreeMarkerUtil {

    private static final String LT = "<";
    private static final String LT_CHAR = "&lt;";
    private static final String GT = ">";
    private static final String GT_CHAR = "&gt;";
    private static final String AMP = "&";
    private static final String AMP_CHAR = "&amp;";
    private static final String APOS = "'";
    private static final String APOS_CHAR = "&apos;";
    private static final String QUOT = "&quot;";
    private static final String QUOT_CHAR = "\"";

    /**
     * Template to String Method Note
     *
     * @param templateContent template content
     * @param map             tempate data map
     * @return
     */
    public static String template2String(String templateContent, Map<String, Object> map,
                                         boolean isNeedFilter) {
        if (StringUtils.isEmpty(templateContent)) {
            return null;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        Map<String, Object> newMap = new HashMap<>(1);

        Set<String> keySet = map.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                Object o = map.get(key);
                if (o != null) {
                    if (o instanceof String) {
                        String value = o.toString();
                        value = value.trim();
                        if (isNeedFilter) {
                            value = filterXmlString(value);
                        }
                        newMap.put(key, value);
                    } else {
                        newMap.put(key, o);
                    }
                }
            }
        }
        Template t = null;
        try {
            // 设定freemarker对数值的格式化
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setNumberFormat("#");
            t = new Template("", new StringReader(templateContent), cfg);
            StringWriter writer = new StringWriter();
            t.process(newMap, writer);
            return writer.toString();
        } catch (IOException e) {
            log.error("TemplateUtil -> template2String IOException.", e);
        } catch (TemplateException e) {
            log.error("TemplateUtil -> template2String TemplateException.", e);
        } finally {
            newMap.clear();
            newMap = null;
        }
        return null;
    }

    private static String filterXmlString(String str) {
        if (null == str) {
            return null;
        }
        str = str.replaceAll(LT, LT_CHAR);
        str = str.replaceAll(GT, GT_CHAR);
        str = str.replaceAll(AMP, AMP_CHAR);
        str = str.replaceAll(APOS, APOS_CHAR);
        str = str.replaceAll(QUOT, QUOT_CHAR);
        return str;
    }
}
