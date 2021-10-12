package com.zhouzifei.tool.consts;

import javafx.scene.chart.ValueAxis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27

 * @Description
 */
public enum StorageTypeConst {

    ALIYUN("aliyun","阿里云免费空间40G,免费流量10G/月 （限定新用户、限时6个月）https://www.aliyun.com/product/oss"),
    QINIUYUN("qiniu", "七牛云免费空间10G,免费流量10G/月,免费GET100万次/月https://www.qiniu.com/prices"),
    LOCAL("local", "本地上传"),
    TENGXUNYUN("tengxunyun", "腾讯云COS免费空间50G,免费流量10G/月 https://cloud.tencent.com/product/cos/option>"),
    YOUPAIYUN("youpaiyun", "又拍云免费空间10G,免费流量15G/月(非开通就有,需要额外申请又拍云联盟,限时1年）https://www.upyun.com/league"),
    HUAWEIYUN("huaweiyun", "HUAWEI"),
    FASTDFS("fastdfs", "FAST-DFS"),
    SMMS("smms", "SMMS"),
    XMLY("xmly", "XMLY"),
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
    public static Map<Object, Object> getMap() {
        StorageTypeConst[] alarmGrades = StorageTypeConst.values();
        Map<Object, Object> map = new HashMap<>();
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
        final Map<Object, Object> values = StorageTypeConst.getMap();
        System.out.println(values);
    }
}
