package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表 (MySQL 主库)
 * 支持多角色：ADMIN / INTERVIEWER / CANDIDATE / USER
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /** 用户名（登录用） */
    private String username;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 状态: 0=禁用, 1=启用, 2=锁定 */
    private Integer status;

    /** 最后登录时间 */
    private java.time.LocalDateTime lastLoginTime;

    /** 最后登录 IP */
    private String lastLoginIp;
}
