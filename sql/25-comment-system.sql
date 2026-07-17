-- ============================================================
-- 25-comment-system.sql
-- Comment module tables and permissions.
-- ============================================================

USE yimian;

CREATE TABLE IF NOT EXISTS sys_comment (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    target_type         VARCHAR(20) NOT NULL COMMENT 'Comment target type: knowledge/blog',
    target_id           BIGINT NOT NULL COMMENT 'Target ID',
    parent_id           BIGINT NULL COMMENT 'Top-level parent comment ID; null for root comments',
    reply_to_comment_id BIGINT NULL COMMENT 'Comment being replied to',
    reply_to_user_id    BIGINT NULL COMMENT 'User being replied to',
    author_id           BIGINT NOT NULL COMMENT 'Author user ID',
    content             VARCHAR(1000) NOT NULL COMMENT 'Comment content',
    like_count          INT DEFAULT 0 COMMENT 'Like count',
    reply_count         INT DEFAULT 0 COMMENT 'Reply count',
    status              TINYINT DEFAULT 1 COMMENT 'Status: 1=normal',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted             TINYINT DEFAULT 0 COMMENT 'Logic delete flag',

    INDEX idx_comment_target_parent (target_type, target_id, parent_id, deleted),
    INDEX idx_comment_parent (parent_id, deleted),
    INDEX idx_comment_author (author_id),
    INDEX idx_comment_hot (target_type, target_id, parent_id, deleted, like_count, reply_count, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comment table';

CREATE TABLE IF NOT EXISTS sys_comment_like (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    comment_id  BIGINT NOT NULL COMMENT 'Comment ID',
    user_id     BIGINT NOT NULL COMMENT 'User ID',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted     TINYINT DEFAULT 0 COMMENT 'Logic delete flag',

    INDEX idx_comment_like_comment_user (comment_id, user_id, deleted),
    INDEX idx_comment_like_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comment like table';

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('comment:view',   'View comments',   'View comments'),
('comment:create', 'Create comments', 'Create comments and replies'),
('comment:like',   'Like comments',   'Like or unlike comments')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'comment:view',
    'comment:create',
    'comment:like'
)
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);

INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('COMMENT', 'Comment module', 11, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort = VALUES(sort),
    enabled = VALUES(enabled);
