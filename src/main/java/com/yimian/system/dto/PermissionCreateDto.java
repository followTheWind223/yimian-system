package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增权限 DTO
 */
@Data
public class PermissionCreateDto {

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 64)
    private String permCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 64)
    private String permName;

    @Size(max = 255)
    private String description;
}
