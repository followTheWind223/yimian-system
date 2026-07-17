-- ============================================================
-- 26-knowledge-like.sql
-- Knowledge like table and permission.
-- ============================================================

USE yimian;

CREATE TABLE IF NOT EXISTS sys_knowledge_like (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    knowledge_id  BIGINT NOT NULL COMMENT 'Knowledge ID',
    user_id       BIGINT NOT NULL COMMENT 'User ID',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted       TINYINT DEFAULT 0 COMMENT 'Logic delete flag',

    UNIQUE KEY uk_knowledge_user (knowledge_id, user_id),
    INDEX idx_knowledge_like_knowledge_user_deleted (knowledge_id, user_id, deleted),
    INDEX idx_knowledge_like_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Knowledge like table';

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('knowledge:like', 'Like knowledge', 'Like or unlike knowledge')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code = 'knowledge:like'
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);
