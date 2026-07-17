package com.yimian.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑角色 DTO
 */
@Data
public class RoleUpdateDto {

    @Size(max = 32)
    private String roleName;

    @Size(max = 255)
    private String description;

    private Integer sort;
}
