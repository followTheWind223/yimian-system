package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 管理员新增用户 DTO
 */
@Data
public class UserCreateDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 64)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32)
    private String password;

    private String email;
    private String phone;

    @Size(max = 64)
    private String nickname;

    /** 角色编码列表 */
    private List<String> roleCodes;
}
