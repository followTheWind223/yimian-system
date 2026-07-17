package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {

    private Long id;
    private String targetType;
    private Long targetId;
    private Long parentId;
    private Long replyToCommentId;
    private Long replyToUserId;
    private String replyToNickname;
    private Long authorId;
    private String authorNickname;
    private String authorAvatar;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private Integer status;
    private Boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentVO> replies;
}
