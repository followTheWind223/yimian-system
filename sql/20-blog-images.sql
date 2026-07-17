-- ============================================================
-- 20-blog-images.sql
-- 博客图片模块：移除封面 URL，新增多图支持（至多 9 张）
-- 依赖：17-init-blog.sql（博客表已创建）
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 20-blog-images.sql
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 博客图片表（每篇博客至多 9 张图）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_blog_image (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    blog_id     BIGINT       NOT NULL                   COMMENT '所属博客ID',
    url         VARCHAR(500) NOT NULL                   COMMENT '图片访问URL',
    sort        INT          DEFAULT 0                  COMMENT '排序号（1~9）',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    deleted     TINYINT      DEFAULT 0                  COMMENT '逻辑删除',

    INDEX idx_blog_id (blog_id),
    INDEX idx_blog_deleted (blog_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客图片表';

-- ============================================================
-- 2. 博客表结构调整：移除 cover_image 和 tags，新增 topic_ids
--    用存储过程兼容 MySQL 5.7+，避免 DROP COLUMN IF EXISTS 语法差异
-- ============================================================
DROP PROCEDURE IF EXISTS proc_alter_blog_20;

DELIMITER //

CREATE PROCEDURE proc_alter_blog_20()
BEGIN
    -- 删除 cover_image
    IF EXISTS (SELECT 1 FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_blog' AND COLUMN_NAME = 'cover_image') THEN
        ALTER TABLE sys_blog DROP COLUMN cover_image;
    END IF;

    -- 删除 tags
    IF EXISTS (SELECT 1 FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_blog' AND COLUMN_NAME = 'tags') THEN
        ALTER TABLE sys_blog DROP COLUMN tags;
    END IF;

    -- 添加 topic_ids
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_blog' AND COLUMN_NAME = 'topic_ids') THEN
        ALTER TABLE sys_blog ADD COLUMN topic_ids VARCHAR(200) DEFAULT NULL COMMENT '关联话题ID列表，逗号分隔（冗余，便于列表直接展示）' AFTER ref_id;
    END IF;
END //

DELIMITER ;

CALL proc_alter_blog_20();
DROP PROCEDURE IF EXISTS proc_alter_blog_20;
