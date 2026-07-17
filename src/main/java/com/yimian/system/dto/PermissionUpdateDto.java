package com.yimian.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑权限 DTO
 */
@Data
public class PermissionUpdateDto {

    @Size(max = 64)
    private String permName;

    @Size(max = 255)
    private String description;
}
