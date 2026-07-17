-- ============================================================
-- 15-init-favorite-folder.sql
-- 用户收藏夹模块初始化脚本
-- 执行: mysql -u root -p1234 --default-character-set=utf8mb4 yimian < 15-init-favorite-folder.sql
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 收藏夹表
-- 用户可创建多个收藏夹，并选择公开/私有
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_favorite_folder (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    name          VARCHAR(100) NOT NULL                  COMMENT '收藏夹名称',
    description   VARCHAR(500) DEFAULT NULL              COMMENT '收藏夹描述',
    user_id       BIGINT       NOT NULL                  COMMENT '所属用户ID',
    is_public     TINYINT      DEFAULT 0                 COMMENT '是否公开: 0=私有, 1=公开',
    cover_image   VARCHAR(500) DEFAULT NULL              COMMENT '封面图URL',
    item_count    INT          DEFAULT 0                 COMMENT '收藏条目数',
    view_count    INT          DEFAULT 0                 COMMENT '浏览次数',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                         COMMENT '更新时间',
    deleted       TINYINT      DEFAULT 0                 COMMENT '逻辑删除',

    INDEX idx_user_id (user_id),
    INDEX idx_public_deleted (is_public, deleted),
    INDEX idx_user_deleted (user_id, deleted),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏夹表';

-- ============================================================
-- 2. 收藏条目表
-- 一条记录表示某个知识题目被加入某个收藏夹
-- 逻辑删除场景下不使用唯一索引，重复收藏由 Service 层校验
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_favorite_item (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    folder_id    BIGINT   NOT NULL                      COMMENT '所属收藏夹ID',
    knowledge_id BIGINT   NOT NULL                      COMMENT '收藏的知识ID',
    sort         INT      DEFAULT 0                     COMMENT '排序号',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP     COMMENT '收藏时间',
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                        COMMENT '更新时间',
    deleted      TINYINT  DEFAULT 0                     COMMENT '逻辑删除',

    INDEX idx_folder_id (folder_id),
    INDEX idx_knowledge_id (knowledge_id),
    INDEX idx_folder_deleted (folder_id, deleted),
    INDEX idx_folder_knowledge_deleted (folder_id, knowledge_id, deleted),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏条目表';
