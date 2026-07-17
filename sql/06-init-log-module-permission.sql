-- ============================================================
-- 日志模块字典权限初始化
-- ============================================================

INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('log:module:list', '查看日志模块字典', '查看模块编码/名称/排序/启停'),
('log:module:edit', '管理日志模块字典', '新增/编辑/删除模块字典')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ADMIN 拥有日志模块字典权限
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('log:module:list', 'log:module:edit')
  AND (p.deleted IS NULL OR p.deleted = 0);
