package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志模块字典
 * sys_log_module 表 —— 流水表 sys_operation_log.module 存编码，查询时 JOIN 本表翻译为 name
 */
@Data
@TableName("sys_log_module")
public class LogModule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模块编码，对应 @OperationLog(module="USER") */
    private String code;

    /** 模块名称（展示用），如 "用户管理" */
    private String name;

    /** 模块描述 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 启用状态：0=禁用，1=启用 */
    private Integer enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
