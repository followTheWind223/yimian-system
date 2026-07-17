package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类（所有 MySQL 实体继承此类）
 *
 * 时间字段策略：created_at / updated_at 均由 MySQL 兜底
 * - created_at：DDL 为 DEFAULT CURRENT_TIMESTAMP，insert 时不传该列（FieldStrategy.NEVER）
 * - updated_at：DDL 为 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP，
 *   insert/update 时都不传该列，由 MySQL 自动维护
 * 不再依赖 MyBatis-Plus 的 MetaObjectHandler 自动填充，避免填充未触发时写入 null 覆盖默认值。
 */
@Data
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建时间（由 MySQL DEFAULT CURRENT_TIMESTAMP 自动填充） */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;

    /** 更新时间（由 MySQL ON UPDATE CURRENT_TIMESTAMP 自动维护） */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted = 0;
}
