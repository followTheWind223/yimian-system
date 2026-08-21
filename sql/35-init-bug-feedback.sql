-- Bug feedback system
-- Create table for user-submitted server bug and issue feedback.

CREATE TABLE IF NOT EXISTS sys_bug_feedback (
  id BIGINT NOT NULL COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '反馈用户ID',
  username VARCHAR(64) NOT NULL COMMENT '反馈用户名快照',
  title VARCHAR(120) NOT NULL COMMENT '反馈标题',
  content TEXT NOT NULL COMMENT '问题描述',
  page_url VARCHAR(500) DEFAULT NULL COMMENT '问题所在页面',
  contact VARCHAR(120) DEFAULT NULL COMMENT '联系方式',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待处理 1处理中 2已解决 3忽略',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_status_created_at (status, created_at),
  KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户问题反馈表';

INSERT INTO sys_log_module (code, name, description, sort, enabled)
SELECT 'FEEDBACK', '问题反馈', '用户问题反馈提交和处理记录', 95, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_log_module WHERE code = 'FEEDBACK');
