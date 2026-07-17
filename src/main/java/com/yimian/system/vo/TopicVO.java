package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicVO {

    private Long id;
    private String name;
    private String description;
    private String color;
    private Integer sort;
    private Integer blogCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
