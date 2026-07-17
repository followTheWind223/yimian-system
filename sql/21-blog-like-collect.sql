-- ============================================================
-- 21-blog-like-collect.sql
-- 博客互动表：点赞记录、收藏记录
-- 依赖：17-init-blog.sql
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 21-blog-like-collect.sql
-- ============================================================

USE yimian;

CREATE TABLE IF NOT EXISTS sys_blog_like (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    blog_id     BIGINT   NOT NULL                       COMMENT '博客ID',
    user_id     BIGINT   NOT NULL                       COMMENT '点赞用户ID',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP      COMMENT '点赞时间',
    deleted     TINYINT  DEFAULT 0                      COMMENT '逻辑删除（取消点赞时标记）',

    UNIQUE KEY uk_blog_user (blog_id, user_id),
    INDEX idx_blog_id (blog_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客点赞记录表';

CREATE TABLE IF NOT EXISTS sys_blog_collect (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    blog_id     BIGINT   NOT NULL                       COMMENT '博客ID',
    folder_id   BIGINT   NOT NULL                       COMMENT '收藏夹ID',
    user_id     BIGINT   NOT NULL                       COMMENT '收藏用户ID',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP      COMMENT '收藏时间',
    deleted     TINYINT  DEFAULT 0                      COMMENT '逻辑删除',

    INDEX idx_blog_id (blog_id),
    INDEX idx_folder_id (folder_id),
    INDEX idx_user_id (user_id),
    INDEX idx_blog_user_deleted (blog_id, user_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客收藏记录表';
