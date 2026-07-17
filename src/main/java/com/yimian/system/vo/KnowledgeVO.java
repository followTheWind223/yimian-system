package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识题目 VO（脱敏返回）
 */
@Data
public class KnowledgeVO {

    private Long id;
    private String title;
    private String content;
    private Integer difficulty;
    private Integer status;
    private String auditRemark;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private Long submitUserId;

    /** 提交人昵称 */
    private String submitUserName;
    private String submitUserAvatar;

    /** 审核人昵称 */
    private String auditUserName;
    private String auditUserAvatar;

    private Integer viewCount;
    private Integer likeCount;
    private Boolean liked;
    private Integer collectCount;
    private Integer commentCount;

    /** 关联标签列表 */
    private List<TagVO> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class TagVO {
        private Long id;
        private String name;
        private String color;
    }
}
