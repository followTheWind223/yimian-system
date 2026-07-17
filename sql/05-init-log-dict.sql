-- ============================================================
-- 操作日志模块字典表
-- 统一管理操作日志的模块编码与中文名，供前端筛选下拉框动态加载
--
-- 配合关系：
--   sys_log_module    —— 模块字典（本表，只定义有哪些模块）
--   sys_operation_log —— 操作流水表（03 脚本，已存在，不动结构）
--
-- 工作方式：
--   @OperationLog(module="USER", operation="新增用户")
--   - module 传编码，写入 sys_operation_log.module
--   - operation 仍存中文字符串，直接展示，不单独建字典
--   - 查询日志列表时 JOIN sys_log_module 把 module 编码翻译成 name 返回前端
--   - 模块改名只需改字典一处，历史日志也跟着变
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_log_module (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    code        VARCHAR(32)  NOT NULL                  COMMENT '模块编码，对应注解 @OperationLog(module=...)，如 USER',
    name        VARCHAR(64)  NOT NULL                  COMMENT '模块名称（展示用），如 用户管理',
    description VARCHAR(255) DEFAULT NULL              COMMENT '模块描述',
    sort        INT          DEFAULT 0                 COMMENT '排序号',
    enabled     TINYINT      DEFAULT 1                 COMMENT '启用状态：0=禁用，1=启用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                           COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志模块字典';

-- ============================================================
-- 预置模块数据（编码已存在则刷新 name/sort，便于调整中文名和顺序）
-- 依据现有 @OperationLog 注解的 module 值梳理
-- ============================================================
INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('USER',       '用户管理', 1, 1),
('ROLE',       '角色管理', 2, 1),
('PERMISSION', '权限管理', 3, 1),
('PROFILE',    '个人中心', 4, 1),
('AUTH',       '认证',     5, 1),
('LOG_MODULE', '日志模块字典', 6, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort);
