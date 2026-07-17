package com.yimian.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BlogQueryDto {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    @Max(50)
    private Integer size = 10;

    private String keyword;

    /** newest / hot */
    private String sort;

    /** knowledge / folder */
    private String refType;
}
