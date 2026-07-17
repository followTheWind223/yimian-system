-- ============================================================
-- 28-user-public-follow-permissions.sql
-- Patch permissions for public user profile and follow APIs.
-- ============================================================

USE yimian;

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
WHERE (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);
