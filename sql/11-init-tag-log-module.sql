-- ============================================================
-- 日志模块字典：新增 TAG 模块编码
-- 配合 OperationLogModuleSyncRunner 启动时自动同步
-- ============================================================

USE yimian;

INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('TAG', '标签模块', 8, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort);
