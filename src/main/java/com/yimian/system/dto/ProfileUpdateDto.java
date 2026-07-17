package com.yimian.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改个人信息 DTO（不含密码）
 */
@Data
public class ProfileUpdateDto {

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 64)
    private String nickname;

    @Size(max = 512, message = "头像URL过长")
    private String avatar;
}
