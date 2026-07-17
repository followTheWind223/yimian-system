package com.yimian.system.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标记在 Controller 方法上，AOP 自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 模块名称，如"用户管理""角色管理" */
    String module();

    /** 操作类型，如"创建用户""删除角色" */
    String operation();

    /** 动态描述，支持 SpEL 表达式，如 用户 #{#dto.username} 登录 */
    String description() default "";

    /** 是否记录请求参数（默认 true，敏感字段自动脱敏） */
    boolean logParams() default true;

    /** 是否记录响应体（默认 false） */
    boolean logResult() default false;
}
