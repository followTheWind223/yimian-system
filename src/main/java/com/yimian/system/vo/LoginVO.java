package com.yimian.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 VO
 */
@Data
@Builder
public class LoginVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 访问 Token */
    private String accessToken;

    /** 刷新 Token */
    private String refreshToken;

    /** Token 类型 */
    private String tokenType;

    /** 过期时间（秒） */
    private Long expiresIn;

    /** 权限编码列表 */
    private java.util.List<String> permissions;

    /** 用户信息 */
    private UserVO userInfo;
}
