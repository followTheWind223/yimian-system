-- ============================================================
-- 16-favorite-permissions.sql
-- 收藏夹模块权限初始化脚本
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian --execute="source system/sql/16-favorite-permissions.sql"
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 收藏夹模块权限
-- ============================================================
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('favorite:create',      '创建收藏夹',       '创建个人收藏夹'),
('favorite:edit',        '编辑收藏夹',       '编辑自己的收藏夹'),
('favorite:delete',      '删除收藏夹',       '删除自己的收藏夹'),
('favorite:item:add',    '收藏题目',         '将知识题目加入自己的收藏夹'),
('favorite:item:delete', '取消收藏',         '从自己的收藏夹移除知识题目'),
('favorite:list',        '查看收藏夹列表',   '查看公开收藏夹和自己的收藏夹'),
('favorite:view',        '查看收藏夹详情',   '查看公开收藏夹详情和自己的收藏状态')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

-- ============================================================
-- 2. 普通用户和管理员拥有收藏夹基础权限
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN (
    'favorite:create',
    'favorite:edit',
    'favorite:delete',
    'favorite:item:add',
    'favorite:item:delete',
    'favorite:list',
    'favorite:view'
)
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);

-- ============================================================
-- 3. 操作日志模块字典
-- ============================================================
INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('FAVORITE', '收藏夹模块', 9, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort = VALUES(sort),
    enabled = VALUES(enabled);

-- ============================================================
-- 4. 验证
-- ============================================================
SELECT id, perm_code, perm_name
FROM sys_permission
WHERE perm_code LIKE 'favorite:%'
ORDER BY id;

SELECT r.role_code, p.perm_code
FROM sys_role_permission rp
JOIN sys_role r ON rp.role_id = r.id
JOIN sys_permission p ON rp.perm_id = p.id
WHERE p.perm_code LIKE 'favorite:%'
ORDER BY r.role_code, p.perm_code;
