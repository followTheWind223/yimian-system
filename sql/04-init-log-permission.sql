-- ============================================================
-- 操作日志权限初始化
-- ============================================================

-- 日志管理
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('log:list', '查看操作日志', '管理员查看操作日志列表')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ADMIN 拥有日志查看权限
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code = 'log:list'
WHERE r.role_code = 'ROLE_ADMIN'
  AND (p.deleted IS NULL OR p.deleted = 0);
