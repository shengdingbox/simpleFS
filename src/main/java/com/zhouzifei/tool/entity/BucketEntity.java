package com.zhouzifei.tool.entity;

import com.aliyun.oss.model.CannedAccessControlList;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BucketEntity extends AbstractEntity {

    /**
     * 私有读写	      CannedAccessControlList.Private <br>
     * 公共读私有写	  CannedAccessControlList.PublicRead <br>
     * 公共读写	      CannedAccessControlList.PublicReadWrite
     */
    private CannedAccessControlList acl;

    public BucketEntity(String bucketName) {
        super(bucketName);
    }

    public BucketEntity setAcl(CannedAccessControlList acl) {
        this.acl = acl;
        return this;
    }
}
