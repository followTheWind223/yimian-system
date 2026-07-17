package com.yimian.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员编辑用户 DTO
 */
@Data
public class UserUpdateDto {

    private String email;
    private String phone;

    @Size(max = 64)
    private String nickname;

    /** 状态: 0=禁用, 1=启用, 2=锁定 */
    private Integer status;
}
