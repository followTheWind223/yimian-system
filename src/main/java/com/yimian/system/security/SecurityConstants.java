package com.yimian.system.security;

import java.util.List;

/**
 * 安全常量
 */
public final class SecurityConstants {

    /** 所有权限编码（ADMIN 无需查 DB，直接持有全部） */
    public static final List<String> ALL_PERMISSIONS = List.of(
            // 用户管理
            "user:list", "user:view", "user:create", "user:edit",
            "user:delete", "user:reset-password", "user:assign-roles",
            "user:view-public", "user:follow",
            // 角色管理
            "role:list", "role:view", "role:create", "role:edit",
            "role:delete", "role:assign-perms",
            // 权限管理
            "perm:list", "perm:view", "perm:create", "perm:edit", "perm:delete",
            // 日志管理
            "log:list",
            // 日志模块字典
            "log:module:list", "log:module:edit",
            // 知识管理
            "knowledge:direct-upload", "knowledge:audit",
            "knowledge:submit", "knowledge:edit", "knowledge:delete",
            "knowledge:list", "knowledge:view", "knowledge:like",
            // 标签管理
            "tag:manage", "tag:list", "tag:create", "tag:edit", "tag:delete",
            // 收藏夹
            "favorite:create", "favorite:edit", "favorite:delete",
            "favorite:item:add", "favorite:item:delete",
            "favorite:list", "favorite:view",
            // 博客
            "blog:create", "blog:edit", "blog:delete", "blog:list", "blog:view",
            "blog:like", "blog:collect",
            "comment:view", "comment:create", "comment:like",
            // 个人中心
            "self:profile", "self:change-password",
            // Agent 会话审计
            "agent:conversation:list", "agent:conversation:view",
            // Agent 用量统计
            "agent:statistics:view"
    );

    private SecurityConstants() {}
}
