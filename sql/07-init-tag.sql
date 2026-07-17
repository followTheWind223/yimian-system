-- ============================================================
-- 标签表初始化脚本
-- 扁平标签结构，通过关联表与知识模块多对多绑定
-- ============================================================

-- ============================================================
-- 标签表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_tag (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    name        VARCHAR(50)   NOT NULL                  COMMENT '标签名称（如"Java"）',
    sort        INT           DEFAULT 0                 COMMENT '排序号',
    color       VARCHAR(20)   DEFAULT NULL              COMMENT '标签颜色（前端展示用，如#6366f1）',
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                         COMMENT '更新时间',
    deleted     TINYINT       DEFAULT 0                 COMMENT '逻辑删除',

    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================================
-- 初始数据：预置标签
-- ============================================================
INSERT INTO sys_tag (id, name, sort, color) VALUES
(1,  'Java',          1,  '#e74c3c'),
(2,  'Java基础',      2,  '#e74c3c'),
(3,  'JVM',           3,  '#e74c3c'),
(4,  '多线程',        4,  '#e74c3c'),
(5,  '集合框架',      5,  '#e74c3c'),
(6,  'Spring',        6,  '#6db33f'),
(7,  'Spring Boot',   7,  '#6db33f'),
(8,  'Spring Cloud',  8,  '#6db33f'),
(9,  'Spring MVC',    9,  '#6db33f'),
(10, 'MyBatis',       10, '#6db33f'),
(11, 'MySQL',         11, '#336791'),
(12, 'Redis',         12, '#336791'),
(13, 'PostgreSQL',    13, '#336791'),
(14, 'RabbitMQ',      14, '#f39c12'),
(15, 'Kafka',         15, '#f39c12'),
(16, 'Elasticsearch', 16, '#f39c12'),
(17, '微服务',        17, '#9b59b6'),
(18, 'RPC',           18, '#9b59b6'),
(19, '分布式事务',    19, '#9b59b6'),
(20, '计算机网络',    20, '#3498db'),
(21, '操作系统',      21, '#1abc9c'),
(22, '数据结构',      22, '#e67e22'),
(23, '设计模式',      23, '#95a5a6'),
(24, '前端',          24, '#2ecc71'),
(25, 'DevOps',        25, '#34495e'),
(26, 'Python',        26, '#3776ab');
