package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增角色 DTO
 */
@Data
public class RoleCreateDto {

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 32)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 32)
    private String roleName;

    @Size(max = 255)
    private String description;

    private Integer sort;
}
