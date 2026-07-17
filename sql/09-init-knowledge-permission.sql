-- ============================================================
-- 知识模块权限初始化
-- ============================================================

USE yimian;

-- 知识管理权限
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('knowledge:direct-upload', '直接上传题目', '跳过审核直接发布题目（需此权限）'),
('knowledge:audit',         '审核题目',     '审核知识题目（通过/拒绝）')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ============================================================
-- ADMIN 拥有全部权限（增量：新权限自动分配）
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND (p.deleted IS NULL OR p.deleted = 0);
