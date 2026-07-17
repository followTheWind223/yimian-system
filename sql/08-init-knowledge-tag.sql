-- ============================================================
-- 知识模块初始化脚本
-- 包含：知识题目表、知识-标签关联表
-- 依赖：07-init-tag.sql（标签表已创建）
-- ============================================================

-- ============================================================
-- 1. 知识题目表
-- 内容统一存储 Markdown 原文，前端负责编辑和渲染
-- 上传 MD 文件仅作为快捷输入方式，不存储原始文件
-- content_hash 用于去重（SHA-256）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_knowledge (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    title          VARCHAR(200)  NOT NULL                  COMMENT '题目（如"HashMap底层原理"）',
    content        LONGTEXT      NOT NULL                  COMMENT '正文（Markdown原文）',
    content_hash   VARCHAR(64)   NOT NULL                  COMMENT '内容SHA-256哈希，用于去重',
    difficulty     TINYINT       DEFAULT 1                 COMMENT '难度：1=简单 2=中等 3=困难',
    status         TINYINT       DEFAULT 0                 COMMENT '状态：0=待审核 1=审核通过 2=审核拒绝 3=草稿',
    audit_remark   VARCHAR(500)  DEFAULT NULL              COMMENT '审核意见（拒绝时填写）',
    audit_user_id  BIGINT        DEFAULT NULL              COMMENT '审核人ID',
    audit_time     DATETIME      DEFAULT NULL              COMMENT '审核时间',
    submit_user_id BIGINT        NOT NULL                  COMMENT '提交人ID',
    view_count     INT           DEFAULT 0                 COMMENT '浏览次数',
    like_count     INT           DEFAULT 0                 COMMENT '点赞数',
    collect_count  INT           DEFAULT 0                 COMMENT '被收藏总次数（冗余，方便排序）',
    comment_count  INT           DEFAULT 0                 COMMENT '评论数（冗余）',
    rag_doc_ids    TEXT          DEFAULT NULL              COMMENT 'RAG入库的文档ID列表（JSON数组）',
    created_at     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                            COMMENT '更新时间',
    deleted        TINYINT       DEFAULT 0                 COMMENT '逻辑删除',

    UNIQUE KEY uk_content_hash (content_hash),
    INDEX idx_submit_user_id (submit_user_id),
    INDEX idx_status (status, created_at),
    INDEX idx_view_count (view_count),
    INDEX idx_difficulty (difficulty),
    FULLTEXT INDEX ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识题目表';

-- ============================================================
-- 2. 知识-标签关联表（多对多）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_knowledge_tag (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    knowledge_id BIGINT NOT NULL                    COMMENT '知识ID',
    tag_id       BIGINT NOT NULL                    COMMENT '标签ID',

    UNIQUE KEY uk_knowledge_tag (knowledge_id, tag_id),
    INDEX idx_knowledge_id (knowledge_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识-标签关联表';
