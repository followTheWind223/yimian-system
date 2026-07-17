package com.yimian.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户响应 VO（脱敏后的用户信息）
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private java.util.List<String> roles;
    private java.util.List<String> permissions;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTime;
    private Integer followingCount;
    private Integer followerCount;
    private Boolean followed;
}
