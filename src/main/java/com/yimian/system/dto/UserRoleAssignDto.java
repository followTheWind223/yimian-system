package com.yimian.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户分配角色 DTO
 */
@Data
public class UserRoleAssignDto {

    @NotNull(message = "角色列表不能为空")
    private List<String> roleCodes;
}
