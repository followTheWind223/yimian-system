-- System announcement.
-- Important announcements are shown in a user-side popup until confirmed.
-- All announcements are also delivered as rows in sys_notification.

CREATE TABLE IF NOT EXISTS sys_system_announcement (
  id BIGINT NOT NULL COMMENT 'Primary key',
  title VARCHAR(120) NOT NULL COMMENT 'Announcement title',
  content TEXT NOT NULL COMMENT 'Announcement content',
  important TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1=show popup, 0=notification only',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1=enabled, 0=disabled',
  creator_id BIGINT NOT NULL COMMENT 'Admin user ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete flag',
  PRIMARY KEY (id),
  KEY idx_important_enabled_created (important, enabled, created_at),
  KEY idx_creator_created (creator_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System announcement';

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'announcement:list', '系统公告-列表', '查看系统公告发布记录'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'announcement:list');

INSERT INTO sys_permission (perm_code, perm_name, description)
SELECT 'announcement:create', '系统公告-发布', '发布系统公告并投递通知'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'announcement:create');

INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('announcement:list', 'announcement:create')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );

INSERT INTO sys_log_module (code, name, description, sort, enabled)
SELECT 'ANNOUNCEMENT', '系统公告', '管理员发布系统公告并投递用户通知', 96, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_log_module WHERE code = 'ANNOUNCEMENT');
