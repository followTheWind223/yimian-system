-- ============================================================
-- 操作日志表
-- 审计用途：记录所有管理端接口操作，只追加不修改不删除
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    user_id         BIGINT       DEFAULT NULL              COMMENT '操作人ID（未登录时为NULL）',
    username        VARCHAR(64)  DEFAULT NULL              COMMENT '操作人用户名（冗余，避免每次JOIN）',
    module          VARCHAR(50)  NOT NULL                  COMMENT '模块，如"用户管理""角色管理"',
    operation       VARCHAR(50)  NOT NULL                  COMMENT '操作类型，如"创建用户""删除角色"',
    description     VARCHAR(500) DEFAULT NULL              COMMENT '动态描述，如"删除了用户张三(ID:123)"',
    method          VARCHAR(10)  NOT NULL                  COMMENT 'HTTP方法：GET/POST/PUT/DELETE',
    request_uri     VARCHAR(255) NOT NULL                  COMMENT '请求路径，如/api/admin/users/123',
    class_method    VARCHAR(255) DEFAULT NULL              COMMENT '全限定方法名，如com.yimian.system.controller.admin.AdminUserController.deleteUser',
    request_params  TEXT         DEFAULT NULL              COMMENT '请求参数JSON（敏感字段自动脱敏）',
    ip              VARCHAR(45)  DEFAULT NULL              COMMENT '客户端IP（支持IPv6最长45字符）',
    user_agent      VARCHAR(500) DEFAULT NULL              COMMENT '浏览器/客户端User-Agent',
    response_code   INT          DEFAULT NULL              COMMENT 'HTTP状态码：200/400/401/500',
    result_code     INT          DEFAULT NULL              COMMENT '业务码（Result.code）',
    result_msg      VARCHAR(255) DEFAULT NULL              COMMENT '业务消息（Result.message）',
    error_msg       TEXT         DEFAULT NULL              COMMENT '异常信息（仅message不含堆栈）',
    duration        BIGINT       DEFAULT NULL              COMMENT '接口执行耗时（毫秒）',
    status          TINYINT      NOT NULL DEFAULT 1        COMMENT '操作结果：1=成功 0=失败',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志创建时间',

    INDEX idx_user_id      (user_id, created_at),
    INDEX idx_created_at   (created_at),
    INDEX idx_module       (module, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表（只追加，不修改不删除）';
