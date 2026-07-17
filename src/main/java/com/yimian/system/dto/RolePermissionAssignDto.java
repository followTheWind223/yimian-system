package com.yimian.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色分配权限 DTO
 */
@Data
public class RolePermissionAssignDto {

    @NotNull(message = "权限列表不能为空")
    private List<String> permCodes;
}
