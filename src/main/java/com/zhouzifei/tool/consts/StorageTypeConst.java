package com.zhouzifei.tool.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27

 * @Description
 */
public enum StorageTypeConst {

    ALIYUN("aliyun","阿里云OSS"),
    QINIUYUN("qiniu", "七牛云存储"),
    LOCAL("local", "本地上传"),
    TENGXUNYUN("tengxunyun", "腾讯云COS"),
    YOUPAIYUN("youpaiyun", "又拍云存储"),
    HUAWEIYUN("huaweiyun", "华为云存储"),
    FASTDFS("fastdfs", "FAST-DFS"),
    SMMS("smms", "SMMS"),
    XMLY("xmly", "喜马拉雅图床"),
    ;

    private String storageType;
    private String desc;

    StorageTypeConst(String storageType, String desc) {
        this.storageType = storageType;
        this.desc = desc;
}
    public String getStorageType() {
        return storageType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Map<String, String> getMap() {
        StorageTypeConst[] alarmGrades = StorageTypeConst.values();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < alarmGrades.length; i++) {
            final StorageTypeConst alarmGrade = alarmGrades[i];
            map.put(alarmGrade.getStorageType(), alarmGrade.getDesc());
        }
        return map;
    }
    /**
     * 根据Key得到枚举的Value
     * 普通for循环遍历，比较判断
     *
     * @param key
     * @return
     */
    public static StorageTypeConst getEnumType(String key) {
        StorageTypeConst[] alarmGrades = StorageTypeConst.values();
        for (int i = 0; i < alarmGrades.length; i++) {
            if (alarmGrades[i].getStorageType().equals(key)) {
                return alarmGrades[i];
            }
        }
        return StorageTypeConst.ALIYUN;
    }
    //public static final Map<Object, Object> list = Arrays.stream(values()).collect(Collectors.toMap(StorageTypeConst::getStorageType, StorageTypeConst::getDesc));

    public static void main(String[] args) {
        final Map<String, String> values = StorageTypeConst.getMap();
        System.out.println(values);
    }
}
