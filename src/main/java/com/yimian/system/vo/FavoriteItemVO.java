package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteItemVO {

    private Long id;

    private Long folderId;

    private String itemType;

    private Long targetId;

    private Long knowledgeId;

    private Long blogId;

    private Integer sort;

    private KnowledgeVO knowledge;

    private BlogVO blog;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
