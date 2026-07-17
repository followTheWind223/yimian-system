-- inject notification permissions
INSERT IGNORE INTO `sys_permission` (`perm_code`, `perm_name`, `description`)
VALUES ('notification:list', '消息通知-列表', '消息通知列表查询'),
       ('notification:read', '消息通知-标记已读', '标记消息已读'),
       ('notification:delete', '消息通知-删除', '删除消息通知');
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN (
    SELECT id FROM `sys_permission` WHERE `perm_code` IN ('notification:list', 'notification:read', 'notification:delete')
) p
WHERE r.`role_code` IN ('ROLE_ADMIN', 'ROLE_USER');
