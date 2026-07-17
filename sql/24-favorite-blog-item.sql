-- ============================================================
-- 24-favorite-blog-item.sql
-- Extend favorite folders to collect both knowledge items and blogs.
-- Execute: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 24-favorite-blog-item.sql
-- ============================================================

USE yimian;

DROP PROCEDURE IF EXISTS proc_alter_favorite_item_24;

DELIMITER //

CREATE PROCEDURE proc_alter_favorite_item_24()
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                 AND COLUMN_NAME = 'knowledge_id' AND IS_NULLABLE = 'NO') THEN
        ALTER TABLE sys_favorite_item MODIFY COLUMN knowledge_id BIGINT NULL COMMENT 'Knowledge ID, used when item_type=knowledge';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                     AND COLUMN_NAME = 'item_type') THEN
        ALTER TABLE sys_favorite_item ADD COLUMN item_type VARCHAR(20) NOT NULL DEFAULT 'knowledge'
            COMMENT 'Favorite item type: knowledge/blog' AFTER folder_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                     AND COLUMN_NAME = 'target_id') THEN
        ALTER TABLE sys_favorite_item ADD COLUMN target_id BIGINT NULL
            COMMENT 'Target ID for item_type' AFTER item_type;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                     AND COLUMN_NAME = 'blog_id') THEN
        ALTER TABLE sys_favorite_item ADD COLUMN blog_id BIGINT NULL
            COMMENT 'Blog ID, used when item_type=blog' AFTER knowledge_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                     AND INDEX_NAME = 'idx_favorite_item_target') THEN
        ALTER TABLE sys_favorite_item ADD INDEX idx_favorite_item_target (item_type, target_id, deleted);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS
                   WHERE TABLE_SCHEMA = 'yimian' AND TABLE_NAME = 'sys_favorite_item'
                     AND INDEX_NAME = 'idx_folder_target_deleted') THEN
        ALTER TABLE sys_favorite_item ADD INDEX idx_folder_target_deleted (folder_id, item_type, target_id, deleted);
    END IF;
END //

DELIMITER ;

CALL proc_alter_favorite_item_24();
DROP PROCEDURE IF EXISTS proc_alter_favorite_item_24;

UPDATE sys_favorite_item
SET item_type = 'knowledge',
    target_id = knowledge_id
WHERE (item_type IS NULL OR item_type = '' OR item_type = 'knowledge')
  AND target_id IS NULL
  AND knowledge_id IS NOT NULL;

INSERT INTO sys_favorite_item (folder_id, item_type, target_id, knowledge_id, blog_id, sort, created_at, deleted)
SELECT bc.folder_id, 'blog', bc.blog_id, NULL, bc.blog_id, 0, bc.created_at, COALESCE(bc.deleted, 0)
FROM sys_blog_collect bc
WHERE (bc.deleted IS NULL OR bc.deleted = 0)
  AND NOT EXISTS (
      SELECT 1
      FROM sys_favorite_item fi
      WHERE fi.folder_id = bc.folder_id
        AND fi.item_type = 'blog'
        AND fi.target_id = bc.blog_id
        AND (fi.deleted IS NULL OR fi.deleted = 0)
  );
