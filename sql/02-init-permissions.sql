-- ============================================================
-- 权限数据初始化（覆盖所有管理端 CRUD 操作）
-- ============================================================

-- 用户管理
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('user:list',          '查看用户列表', '管理员查看所有用户列表'),
('user:view',          '查看用户详情', '管理员查看单个用户详情'),
('user:create',        '新增用户',     '管理员创建新用户'),
('user:edit',          '编辑用户',     '管理员修改用户昵称/邮箱/手机/状态'),
('user:delete',        '删除用户',     '管理员逻辑删除用户'),
('user:reset-password','重置密码',     '管理员无需旧密码直接重置用户密码'),
('user:assign-roles',  '分配角色',     '管理员给用户分配/更换角色')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 角色管理
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('role:list',         '查看角色列表', '管理员查看所有角色'),
('role:view',         '查看角色详情', '管理员查看单个角色详情及权限'),
('role:create',       '新增角色',     '管理员创建新角色'),
('role:edit',         '编辑角色',     '管理员修改角色名称/描述/排序'),
('role:delete',       '删除角色',     '管理员逻辑删除角色'),
('role:assign-perms', '分配权限',     '管理员给角色分配/更换权限')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 权限管理
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('perm:list',   '查看权限列表', '管理员查看所有权限'),
('perm:view',   '查看权限详情', '管理员查看单个权限详情'),
('perm:create', '新增权限',     '管理员创建新权限'),
('perm:edit',   '编辑权限',     '管理员修改权限名称/描述'),
('perm:delete', '删除权限',     '管理员逻辑删除权限')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 个人中心（普通用户自服务）
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('self:profile',        '个人信息', '查看和修改自己的昵称/邮箱/手机'),
('self:change-password','修改密码', '修改自己的登录密码')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ============================================================
-- ADMIN 拥有全部权限
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND (p.deleted IS NULL OR p.deleted = 0);

-- ============================================================
-- INTERVIEWER 只能查看用户
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('user:list', 'user:view')
WHERE r.role_code = 'ROLE_INTERVIEWER'
  AND (p.deleted IS NULL OR p.deleted = 0);

-- ============================================================
-- 普通用户 / 候选人：仅个人中心自服务
-- ============================================================
INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('self:profile', 'self:change-password')
WHERE r.role_code IN ('ROLE_USER', 'ROLE_CANDIDATE')
  AND (p.deleted IS NULL OR p.deleted = 0);
