package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_comment")
public class Comment extends BaseEntity {

    private String targetType;
    private Long targetId;
    private Long parentId;
    private Long replyToCommentId;
    private Long replyToUserId;
    private Long authorId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private Integer status;
}
