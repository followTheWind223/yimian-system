package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    /** 权限编码，如 user:create */
    private String permCode;

    /** 权限名称，如 新增用户 */
    private String permName;

    /** 权限描述 */
    private String description;
}
