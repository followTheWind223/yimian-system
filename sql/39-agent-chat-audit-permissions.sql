-- Read-only permissions for Agent conversation and token auditing.

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'agent:conversation:list', 'Agent会话-列表', '查看Agent会话统计和会话列表'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_permission WHERE perm_code = 'agent:conversation:list'
);

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'agent:conversation:view', 'Agent会话-详情', '查看Agent会话消息和Token明细'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_permission WHERE perm_code = 'agent:conversation:view'
);

INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('agent:conversation:list', 'agent:conversation:view')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );
