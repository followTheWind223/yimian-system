-- ============================================================
-- 22-blog-like-collect-permissions.sql
-- 博客互动权限初始化脚本
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 22-blog-like-collect-permissions.sql
-- ============================================================

USE yimian;

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('blog:like',    '点赞博客', '点赞或取消点赞博客'),
('blog:collect', '收藏博客', '收藏博客到自己的收藏夹')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('blog:like', 'blog:collect')
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);
