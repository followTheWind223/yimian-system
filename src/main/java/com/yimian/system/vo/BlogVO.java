package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogVO {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Integer status;
    private Integer isPinned;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String refType;
    private Long refId;
    private RefVO ref;
    private List<String> images;
    private List<TopicSimple> topics;
    private Boolean liked;
    private Integer collectCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class RefVO {
        private String type;
        private Long id;
        private String title;
        private String description;
        private String coverImage;
    }

    @Data
    public static class TopicSimple {
        private Long id;
        private String name;
        private String color;
    }
}
