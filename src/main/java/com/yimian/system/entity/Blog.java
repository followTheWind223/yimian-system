package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_blog")
public class Blog extends BaseEntity {

    private String title;
    private String content;
    private String summary;
    private Long authorId;
    private Integer status;
    private Integer isPinned;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String refType;
    private Long refId;
    private String topicIds;
    private LocalDateTime publishedAt;
}
