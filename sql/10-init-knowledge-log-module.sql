-- ============================================================
-- 知识模块日志字典初始化
-- 预置 KNOWLEDGE 模块编码，供 @OperationLog(module="KNOWLEDGE") 使用
-- 配合 OperationLogModuleSyncRunner 启动时自动同步
-- ============================================================

USE yimian;

INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('KNOWLEDGE', '知识模块', 7, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort);
