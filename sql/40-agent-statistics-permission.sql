-- Separate read-only permission for Agent usage statistics.

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'agent:statistics:view', 'Agent统计-查看', '查看Agent Token、成本、模型和会话类型统计'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_permission WHERE perm_code = 'agent:statistics:view'
);

INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'agent:statistics:view'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );
