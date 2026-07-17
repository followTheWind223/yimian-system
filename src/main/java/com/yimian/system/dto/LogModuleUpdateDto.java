package com.yimian.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogModuleUpdateDto {

    @Size(max = 64, message = "模块名称最长 64 字符")
    private String name;

    @Size(max = 255, message = "描述最长 255 字符")
    private String description;

    private Integer sort;

    private Integer enabled;
}
