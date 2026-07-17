package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteFolderVO {

    private Long id;

    private String name;

    private String description;

    private Long userId;

    private Integer isPublic;

    private String coverImage;

    private Integer itemCount;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
