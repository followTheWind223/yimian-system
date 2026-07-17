package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体（只追加，不修改不删除）
 * 不继承 BaseEntity：审计日志不应有逻辑删除和更新时间
 */
@Data
@TableName("sys_operation_log")
public class OperationLogRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人 ID（未登录时为 null） */
    private Long userId;

    /** 操作人用户名（冗余，避免每次 JOIN） */
    private String username;

    /** 模块 */
    private String module;

    /** 操作类型 */
    private String operation;

    /** 动态描述 */
    private String description;

    /** HTTP 方法 */
    private String method;

    /** 请求路径 */
    private String requestUri;

    /** 全限定方法名 */
    private String classMethod;

    /** 请求参数 JSON（敏感字段已脱敏） */
    private String requestParams;

    /** 客户端 IP */
    private String ip;

    /** User-Agent */
    private String userAgent;

    /** HTTP 状态码 */
    private Integer responseCode;

    /** 业务码 */
    private Integer resultCode;

    /** 业务消息 */
    private String resultMsg;

    /** 异常信息（仅 message，不含堆栈） */
    private String errorMsg;

    /** 执行耗时（毫秒） */
    private Long duration;

    /** 操作结果：1=成功 0=失败 */
    private Integer status;

    /** 日志创建时间（由 MySQL DEFAULT CURRENT_TIMESTAMP 自动填充，insert 时不传该列） */
    @com.baomidou.mybatisplus.annotation.TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
