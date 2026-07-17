package com.yimian.system.dto;

import lombok.Data;

@Data
public class CommentQueryDto {

    private String targetType;

    private Long targetId;

    private Integer page = 1;

    private Integer size = 20;

    private String sort = "hot";
}
