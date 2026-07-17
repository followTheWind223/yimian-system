-- ============================================================
-- 27-user-follow.sql
-- User follow table and permissions.
-- ============================================================

USE yimian;

CREATE TABLE IF NOT EXISTS sys_user_follow (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    follower_id  BIGINT NOT NULL COMMENT 'Follower user ID',
    followee_id  BIGINT NOT NULL COMMENT 'Followed user ID',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted      TINYINT DEFAULT 0 COMMENT 'Logic delete flag',

    UNIQUE KEY uk_follower_followee (follower_id, followee_id),
    INDEX idx_user_follow_follower (follower_id, deleted),
    INDEX idx_user_follow_followee (followee_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User follow relation table';

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('user:view-public', 'View public user profile', 'View public user profile'),
('user:follow', 'Follow user', 'Follow or unfollow user')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('user:view-public', 'user:follow')
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);
