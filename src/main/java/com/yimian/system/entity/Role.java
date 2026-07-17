package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /** 角色编码，如 ROLE_ADMIN */
    private String roleCode;

    /** 角色名称，如 管理员 */
    private String roleName;

    /** 角色描述 */
    private String description;

    /** 排序号 */
    private Integer sort;
}
