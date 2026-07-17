-- ============================================================
-- 30-front-runtime-permissions-patch.sql
-- Patch frontend runtime permissions for active roles.
-- ============================================================

USE yimian;

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('user:view-public', 'View public user profile', 'View public user profile'),
('user:follow', 'Follow user', 'Follow or unfollow user'),
('comment:view', 'View comments', 'View comments'),
('comment:create', 'Create comments', 'Create comments and replies'),
('comment:like', 'Like comments', 'Like or unlike comments'),
('knowledge:like', 'Like knowledge', 'Like or unlike knowledge'),
('blog:like', 'Like blog', 'Like or unlike blog'),
('blog:collect', 'Collect blog', 'Collect blog to favorite folder')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'user:view-public',
    'user:follow',
    'comment:view',
    'comment:create',
    'comment:like',
    'knowledge:like',
    'blog:like',
    'blog:collect'
)
WHERE (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);
