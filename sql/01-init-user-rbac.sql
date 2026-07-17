-- ============================================================
-- 用户 RBAC 初始化脚本
-- 包含：用户表、角色表、用户角色关联表、权限表、角色权限关联表
-- ============================================================

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    username        VARCHAR(64)  NOT NULL                  COMMENT '用户名',
    password        VARCHAR(255) NOT NULL                  COMMENT 'BCrypt加密密码',
    email           VARCHAR(128) DEFAULT NULL              COMMENT '邮箱',
    phone           VARCHAR(20)  DEFAULT NULL              COMMENT '手机号',
    nickname        VARCHAR(64)  DEFAULT NULL              COMMENT '昵称',
    avatar          VARCHAR(512) DEFAULT NULL              COMMENT '头像URL',
    status          TINYINT      DEFAULT 1                 COMMENT '状态:0禁用,1启用,2锁定',
    last_login_time DATETIME     DEFAULT NULL              COMMENT '最后登录时间',
    last_login_ip   VARCHAR(64)  DEFAULT NULL              COMMENT '最后登录IP',
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                           COMMENT '更新时间',
    deleted         TINYINT      DEFAULT 0                 COMMENT '逻辑删除',
    UNIQUE KEY uk_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ============================================================
-- 2. 角色表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    role_code   VARCHAR(32)  NOT NULL                  COMMENT '角色编码',
    role_name   VARCHAR(32)  NOT NULL                  COMMENT '角色名称',
    description VARCHAR(255) DEFAULT NULL              COMMENT '角色描述',
    sort        INT          DEFAULT 0                 COMMENT '排序号',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                       COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0                 COMMENT '逻辑删除',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- ============================================================
-- 3. 用户角色关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL                    COMMENT '用户ID',
    role_id BIGINT NOT NULL                    COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ============================================================
-- 4. 权限表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_permission (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '主键',
    perm_code   VARCHAR(64)  NOT NULL                  COMMENT '权限编码',
    perm_name   VARCHAR(64)  NOT NULL                  COMMENT '权限名称',
    description VARCHAR(255) DEFAULT NULL              COMMENT '权限描述',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                       COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0                 COMMENT '逻辑删除',
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- ============================================================
-- 5. 角色权限关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    role_id BIGINT NOT NULL                    COMMENT '角色ID',
    perm_id BIGINT NOT NULL                    COMMENT '权限ID',
    UNIQUE KEY uk_role_perm (role_id, perm_id),
    INDEX idx_role_id (role_id),
    INDEX idx_perm_id (perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 角色
INSERT INTO sys_role (role_code, role_name, sort) VALUES
('ROLE_ADMIN',       '管理员',  1),
('ROLE_INTERVIEWER', '面试官',  2),
('ROLE_CANDIDATE',   '候选人',  3),
('ROLE_USER',        '普通用户', 4);

-- 权限（当前仅用户 CRUD）
INSERT INTO sys_permission (perm_code, perm_name) VALUES
('user:list',   '查看用户列表'),
('user:view',   '查看用户详情'),
('user:create', '新增用户'),
('user:edit',   '编辑用户'),
('user:delete', '删除用户');

-- ADMIN 拥有全部用户 CRUD 权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_ADMIN';

-- INTERVIEWER 只能查看用户
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_INTERVIEWER'
  AND p.perm_code IN ('user:list', 'user:view');

-- 初始 ADMIN 用户（密码: admin123，BCrypt 加密）
INSERT INTO sys_user (username, password, nickname, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 1);

-- 为 admin 分配 ADMIN 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.role_code = 'ROLE_ADMIN';
