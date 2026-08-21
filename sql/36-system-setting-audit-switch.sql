CREATE TABLE IF NOT EXISTS sys_system_setting (
    id BIGINT PRIMARY KEY COMMENT '主键 ID',
    setting_key VARCHAR(100) NOT NULL COMMENT '配置键',
    setting_value VARCHAR(500) NOT NULL COMMENT '配置值',
    remark VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
    UNIQUE KEY uk_setting_key (setting_key),
    KEY idx_deleted (deleted)
) COMMENT='系统运行配置表';

INSERT INTO sys_system_setting (id, setting_key, setting_value, remark)
SELECT 360001, 'knowledge.audit.enabled', 'true', '题目提交审核开关：true=提交后待审核，false=提交后直接公开'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_system_setting WHERE setting_key = 'knowledge.audit.enabled'
);
