package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDto {

    @NotBlank
    private String targetType;

    @NotNull
    private Long targetId;

    @NotBlank
    @Size(max = 1000)
    private String content;

    private Long parentId;

    private Long replyToCommentId;

    private Long replyToUserId;
}
