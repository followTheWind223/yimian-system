package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识题目表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_knowledge")
public class Knowledge extends BaseEntity {

    /** 题目 */
    private String title;

    /** 正文（Markdown原文） */
    private String content;

    /** 内容SHA-256哈希，用于去重 */
    private String contentHash;

    /** 难度：1=简单 2=中等 3=困难 */
    private Integer difficulty;

    /** 状态：0=待审核 1=审核通过 2=审核拒绝 3=草稿 */
    private Integer status;

    /** 审核意见（拒绝时填写） */
    private String auditRemark;

    /** 审核人ID */
    private Long auditUserId;

    /** 审核时间 */
    private java.time.LocalDateTime auditTime;

    /** 提交人ID */
    private Long submitUserId;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 被收藏总次数 */
    private Integer collectCount;

    /** 评论数 */
    private Integer commentCount;

    /** RAG入库的文档ID列表（JSON数组） */
    private String ragDocIds;
}
