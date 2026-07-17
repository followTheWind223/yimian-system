package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogModuleCreateDto {

    @NotBlank(message = "模块编码不能为空")
    @Size(max = 32, message = "模块编码最长 32 字符")
    private String code;

    @NotBlank(message = "模块名称不能为空")
    @Size(max = 64, message = "模块名称最长 64 字符")
    private String name;

    @Size(max = 255, message = "描述最长 255 字符")
    private String description;

    private Integer sort;

    private Integer enabled;
}
