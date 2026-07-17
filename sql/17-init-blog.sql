-- ============================================================
-- 17-init-blog.sql
-- 博客讨论模块初始化脚本
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 17-init-blog.sql
-- ============================================================

USE yimian;

CREATE TABLE IF NOT EXISTS sys_blog (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY       COMMENT '主键',
    title          VARCHAR(200) NOT NULL                   COMMENT '标题',
    content        LONGTEXT     NOT NULL                   COMMENT '正文 Markdown',
    summary        VARCHAR(500) DEFAULT NULL               COMMENT '摘要',
    cover_image    VARCHAR(500) DEFAULT NULL               COMMENT '封面图',
    author_id      BIGINT       NOT NULL                   COMMENT '作者ID',
    status         TINYINT      DEFAULT 1                  COMMENT '状态: 0=草稿,1=已发布,2=审核中,3=已屏蔽',
    is_pinned      TINYINT      DEFAULT 0                  COMMENT '是否置顶',
    view_count     INT          DEFAULT 0                  COMMENT '浏览次数',
    like_count     INT          DEFAULT 0                  COMMENT '点赞数',
    comment_count  INT          DEFAULT 0                  COMMENT '评论数',
    ref_type       VARCHAR(20)  DEFAULT NULL               COMMENT '关联类型: knowledge/folder/null',
    ref_id         BIGINT       DEFAULT NULL               COMMENT '关联ID',
    tags           VARCHAR(500) DEFAULT NULL               COMMENT '自定义标签，逗号分隔',
    published_at   DATETIME     DEFAULT NULL               COMMENT '发布时间',
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                          COMMENT '更新时间',
    deleted        TINYINT      DEFAULT 0                  COMMENT '逻辑删除',

    INDEX idx_author_deleted (author_id, deleted),
    INDEX idx_status_deleted (status, deleted),
    INDEX idx_ref (ref_type, ref_id),
    INDEX idx_published_at (published_at),
    INDEX idx_hot (is_pinned, view_count, like_count, comment_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客讨论表';
