-- Permissions for AI provider and model control-plane management.

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'agent:model:list', 'Agent模型-查看', '查看模型供应商、模型和小易配置'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_permission WHERE perm_code = 'agent:model:list'
);

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'agent:model:manage', 'Agent模型-管理', '新增、编辑和测试模型供应商及模型配置'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_permission WHERE perm_code = 'agent:model:manage'
);

INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('agent:model:list', 'agent:model:manage')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );
