-- ============================================================
-- 14-audit-fix-permissions.sql
-- 知识审核权限补全 + 审计用户 ID 字段修复
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 14-audit-fix-permissions.sql
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 确保 knowledge 和 tag 全部权限存在（如果此前未执行 13）
-- ============================================================
INSERT IGNORE INTO sys_permission (id, perm_code, perm_name, description) VALUES
(45, 'knowledge:direct-upload', '直接上传题目', '跳过审核直接发布题目'),
(46, 'knowledge:audit',         '审核题目',     '审核题目（通过/拒绝/批量）'),
(48, 'knowledge:submit',        '提交题目',     '提交题目审核'),
(49, 'knowledge:edit',          '编辑题目',     '编辑自己提交的题目'),
(50, 'knowledge:delete',        '删除题目',     '删除自己提交的题目'),
(51, 'knowledge:list',          '查看题目列表',  '查看题目列表'),
(52, 'knowledge:view',          '查看题目详情',  '查看题目详情'),
(47, 'tag:manage',              '标签管理',     '标签CRUD权限'),
(53, 'tag:list',                '查看标签列表',  '查看标签列表'),
(54, 'tag:create',              '新增标签',     '新增标签'),
(55, 'tag:edit',                '编辑标签',     '编辑标签'),
(56, 'tag:delete',              '删除标签',     '删除标签');

-- ============================================================
-- 2. 将 knowledge 相关权限分配给 ROLE_USER 和 ROLE_ADMIN
--    ROLE_USER: 提交、编辑、删除、查看自己的题目
--    ROLE_ADMIN: 全部知识 + 标签管理权限
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND p.perm_code IN (
    'knowledge:submit', 'knowledge:edit', 'knowledge:delete',
    'knowledge:list', 'knowledge:view', 'knowledge:direct-upload'
  );

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('knowledge:audit', 'tag:manage', 'tag:list', 'tag:create', 'tag:edit', 'tag:delete');

-- ============================================================
-- 3. 验证结果
-- ============================================================
SELECT '=== 知识模块权限 ===' AS '';
SELECT id, perm_code, perm_name FROM sys_permission WHERE perm_code LIKE 'knowledge:%' ORDER BY id;

SELECT '=== 标签模块权限 ===' AS '';
SELECT id, perm_code, perm_name FROM sys_permission WHERE perm_code LIKE 'tag:%' ORDER BY id;

SELECT '=== 角色权限分配（知识 + 标签） ===' AS '';
SELECT r.role_code, p.perm_code
FROM sys_role_permission rp
JOIN sys_role r ON rp.role_id = r.id
JOIN sys_permission p ON rp.perm_id = p.id
WHERE p.perm_code LIKE 'knowledge:%' OR p.perm_code LIKE 'tag:%'
ORDER BY r.role_code, p.perm_code;
