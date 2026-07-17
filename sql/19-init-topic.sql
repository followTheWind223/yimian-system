-- ============================================================
-- 19-init-topic.sql
-- 话题模块初始化脚本
-- 将博客标签系统替换为结构化的话题库
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 19-init-topic.sql
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 话题表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_topic (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    name        VARCHAR(50)   NOT NULL                  COMMENT '话题名称（如"Java并发"）',
    description VARCHAR(200)  DEFAULT NULL              COMMENT '话题简介',
    color       VARCHAR(20)   DEFAULT NULL              COMMENT '话题颜色（前端展示用，如#6366f1）',
    sort        INT           DEFAULT 0                 COMMENT '排序号',
    blog_count  INT           DEFAULT 0                 COMMENT '关联博客数（冗余，方便排序）',
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                         COMMENT '更新时间',
    deleted     TINYINT       DEFAULT 0                 COMMENT '逻辑删除',

    UNIQUE KEY uk_topic_name (name),
    INDEX idx_sort (sort),
    INDEX idx_blog_count (blog_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题表';

-- ============================================================
-- 2. 博客-话题关联表（多对多）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_blog_topic (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    blog_id  BIGINT NOT NULL                    COMMENT '博客ID',
    topic_id BIGINT NOT NULL                    COMMENT '话题ID',

    UNIQUE KEY uk_blog_topic (blog_id, topic_id),
    INDEX idx_blog_id (blog_id),
    INDEX idx_topic_id (topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客-话题关联表';

-- ============================================================
-- 3. 预置话题数据
-- ============================================================
INSERT INTO sys_topic (id, name, description, color, sort) VALUES
(1,  'Java 基础',     'Java 语言核心概念与基础语法',              '#e74c3c', 1),
(2,  'JVM 与性能',    'JVM 内存模型、GC 调优与性能诊断',         '#e74c3c', 2),
(3,  '并发编程',      '多线程、锁机制与并发工具类',               '#e74c3c', 3),
(4,  'Spring 生态',   'Spring Boot / Cloud / MVC 等框架实践',    '#6db33f', 4),
(5,  '数据库',        'MySQL、PostgreSQL 与 SQL 优化',           '#336791', 5),
(6,  '缓存与队列',    'Redis、RabbitMQ、Kafka 等中间件',         '#f39c12', 6),
(7,  '微服务与架构',  '分布式系统、RPC、DDD 与系统设计',         '#9b59b6', 7),
(8,  '计算机网络',    'HTTP、TCP/IP、网络排障',                  '#3498db', 8),
(9,  '操作系统',      'Linux、进程线程与 IO 模型',               '#1abc9c', 9),
(10, '数据结构与算法','常见数据结构、排序与算法题复盘',          '#e67e22', 10),
(11, '设计模式',      'GOF 设计模式与工程实践',                  '#95a5a6', 11),
(12, '前端技术',      'Vue、React、TypeScript 等',               '#2ecc71', 12),
(13, 'DevOps',        'CI/CD、容器化与运维实践',                 '#34495e', 13),
(14, '面试复盘',      '面试经历、追问分析与经验总结',            '#6366f1', 14),
(15, '工程实践',      '代码规范、重构、测试与团队协作',          '#fd79a8', 15),
(16, 'AI 与 LLM',     '大模型、RAG、Agent 与 AI 工程化',         '#a29bfe', 16)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    color = VALUES(color),
    sort = VALUES(sort);

-- ============================================================
-- 4. 更新关联权限
-- ============================================================
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('topic:list',   '查看话题',   '查看话题列表'),
('topic:create', '新增话题',   '管理员新增话题'),
('topic:edit',   '编辑话题',   '管理员编辑话题'),
('topic:delete', '删除话题',   '管理员删除话题')
ON DUPLICATE KEY UPDATE
    perm_name = VALUES(perm_name),
    description = VALUES(description),
    deleted = 0;

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('topic:list')
WHERE r.role_code IN ('ROLE_USER', 'ROLE_ADMIN')
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);

INSERT IGNORE INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.perm_code IN ('topic:create', 'topic:edit', 'topic:delete')
WHERE r.role_code = 'ROLE_ADMIN'
  AND (r.deleted IS NULL OR r.deleted = 0)
  AND (p.deleted IS NULL OR p.deleted = 0);

INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('TOPIC', '话题模块', 11, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort = VALUES(sort),
    enabled = VALUES(enabled);
