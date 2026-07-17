-- ============================================================
-- 18-blog-permissions.sql
-- 博客模块权限初始化脚本
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 18-blog-permissions.sql
-- ============================================================

USE yimian;

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('blog:create', '发布博客', '创建博客或保存草稿'),
('blog:edit',   '编辑博客', '编辑自己的博客'),
('blog:delete', '删除博客', '删除自己的博客'),
('blog:list',   '查看博客列表', '查看已发布博客列表'),
('blog:view',   '查看博客详情', '查看已发布博客详情')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'blog:create',
    'blog:edit',
    'blog:delete',
    'blog:list',
    'blog:view'
)
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);

INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('BLOG', '博客模块', 10, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort = VALUES(sort),
    enabled = VALUES(enabled);

SELECT id, perm_code, perm_name
FROM sys_permission
WHERE perm_code LIKE 'blog:%'
ORDER BY id;
