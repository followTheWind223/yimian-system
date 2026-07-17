-- ============================================================
-- 31 - 消息通知系统（sys_notification）
-- ============================================================

CREATE TABLE IF NOT EXISTS `sys_notification` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `type`            VARCHAR(32)  NOT NULL COMMENT '通知类型：comment_reply / comment_like / knowledge_like / blog_like / new_follower / system',
    `sender_id`       BIGINT       NULL     COMMENT '触发者用户 ID（谁做了这个操作）',
    `receiver_id`     BIGINT       NOT NULL COMMENT '接收者用户 ID（通知给谁）',
    `target_type`     VARCHAR(32)  NULL     COMMENT '关联对象类型：knowledge / blog / comment / user',
    `target_id`       BIGINT       NULL     COMMENT '关联对象 ID',
    `title`           VARCHAR(255) NULL     COMMENT '通知标题（简短描述）',
    `content`         TEXT         NULL     COMMENT '通知正文',
    `extra`           JSON         NULL     COMMENT '扩展数据（JSON）',
    `read`            TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读, 1=已读',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常, 1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_receiver_read` (`receiver_id`, `read`, `created_at` DESC),
    KEY `idx_receiver_type` (`receiver_id`, `type`, `created_at` DESC),
    KEY `idx_sender` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- 插入通知相关的系统权限
INSERT IGNORE INTO `sys_permission` (`code`, `name`, `parent_id`, `path`, `type`, `sort`, `enabled`)
VALUES ('notification:list', '消息通知-列表', NULL, NULL, 1, 90, 1),
       ('notification:read', '消息通知-标记已读', NULL, NULL, 1, 91, 1),
       ('notification:delete', '消息通知-删除', NULL, NULL, 1, 92, 1);

-- 为 ADMIN 和 USER 角色分配通知权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN (
    SELECT id FROM `sys_permission` WHERE `code` IN ('notification:list', 'notification:read', 'notification:delete')
) p
WHERE r.`code` IN ('ROLE_ADMIN', 'ROLE_USER');
