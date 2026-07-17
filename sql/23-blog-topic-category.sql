-- ============================================================
-- 23-blog-topic-category.sql
-- Blog-only topic category tables.
-- Do not reuse sys_tag: knowledge tags and blog topics are separate domains.
-- Execute: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 23-blog-topic-category.sql
-- ============================================================

USE yimian;

-- Blog topic category master table.
CREATE TABLE IF NOT EXISTS sys_blog_topic_category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY       COMMENT 'Primary key',
    name        VARCHAR(50)  NOT NULL                   COMMENT 'Topic category name',
    description VARCHAR(200) DEFAULT NULL               COMMENT 'Topic category description',
    color       VARCHAR(20)  DEFAULT NULL               COMMENT 'Display color',
    sort        INT          DEFAULT 0                  COMMENT 'Sort order',
    blog_count  INT          DEFAULT 0                  COMMENT 'Published blog count',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT 'Created time',
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted     TINYINT      DEFAULT 0                  COMMENT 'Logic delete flag',

    UNIQUE KEY uk_blog_topic_category_name (name),
    INDEX idx_blog_topic_category_sort (sort),
    INDEX idx_blog_topic_category_count (blog_count),
    INDEX idx_blog_topic_category_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog topic category table';

-- Blog-topic relation table.
CREATE TABLE IF NOT EXISTS sys_blog_topic_relation (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY       COMMENT 'Primary key',
    blog_id    BIGINT NOT NULL                         COMMENT 'Blog ID',
    topic_id   BIGINT NOT NULL                         COMMENT 'Blog topic category ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP      COMMENT 'Created time',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    deleted    TINYINT  DEFAULT 0                      COMMENT 'Logic delete flag',

    UNIQUE KEY uk_blog_topic_relation (blog_id, topic_id),
    INDEX idx_blog_topic_relation_blog (blog_id),
    INDEX idx_blog_topic_relation_topic (topic_id),
    INDEX idx_blog_topic_relation_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog-topic relation table';

-- Seed default blog topic categories. Keep IDs compatible with existing topicIds.
INSERT INTO sys_blog_topic_category (id, name, description, color, sort) VALUES
(1,  'Java Basics',          'Java language fundamentals',                       '#e74c3c', 1),
(2,  'JVM Performance',      'JVM memory, GC tuning, and diagnostics',            '#e74c3c', 2),
(3,  'Concurrency',          'Threads, locks, and concurrent utilities',          '#e74c3c', 3),
(4,  'Spring Ecosystem',     'Spring Boot, Cloud, MVC, and related practices',    '#6db33f', 4),
(5,  'Database',             'MySQL, PostgreSQL, and SQL optimization',           '#336791', 5),
(6,  'Cache Queue',          'Redis, RabbitMQ, Kafka, and middleware',            '#f39c12', 6),
(7,  'Microservice Arch',    'Distributed systems, RPC, DDD, and system design',  '#9b59b6', 7),
(8,  'Network',              'HTTP, TCP/IP, and network troubleshooting',         '#3498db', 8),
(9,  'Operating System',     'Linux, processes, threads, and IO models',          '#1abc9c', 9),
(10, 'Data Structure Algo',  'Data structures, sorting, and algorithm reviews',   '#e67e22', 10),
(11, 'Design Pattern',       'GOF patterns and engineering practice',             '#95a5a6', 11),
(12, 'Frontend',             'Vue, React, TypeScript, and frontend engineering',  '#2ecc71', 12),
(13, 'DevOps',               'CI/CD, containers, and operation practice',         '#34495e', 13),
(14, 'Interview Review',     'Interview experience and follow-up analysis',       '#6366f1', 14),
(15, 'Engineering Practice', 'Code quality, refactoring, testing, and teamwork',  '#fd79a8', 15),
(16, 'AI LLM',               'LLM, RAG, Agent, and AI engineering',               '#a29bfe', 16)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    color = VALUES(color),
    sort = VALUES(sort),
    deleted = 0;

-- Migrate data from the previous standalone topic table if it exists.
INSERT INTO sys_blog_topic_category (id, name, description, color, sort, blog_count, created_at, updated_at, deleted)
SELECT id, name, description, color, sort, blog_count, created_at, updated_at, deleted
FROM sys_topic
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    color = VALUES(color),
    sort = VALUES(sort),
    blog_count = GREATEST(sys_blog_topic_category.blog_count, VALUES(blog_count)),
    deleted = VALUES(deleted);

-- Migrate old blog-topic relations into the blog-only relation table.
INSERT IGNORE INTO sys_blog_topic_relation (blog_id, topic_id)
SELECT blog_id, topic_id
FROM sys_blog_topic;

-- Ensure permissions still point at the blog topic module.
INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('TOPIC', 'Blog Topic', 11, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort = VALUES(sort),
    enabled = VALUES(enabled);
