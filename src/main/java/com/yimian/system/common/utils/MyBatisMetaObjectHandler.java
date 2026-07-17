package com.yimian.system.common.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 自动填充处理器
 * 仅填充 deleted（逻辑删除初始值）；created_at / updated_at 已由 MySQL 兜底，不再在此填充。
 */
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // updated_at 由 MySQL ON UPDATE CURRENT_TIMESTAMP 自动维护，无需填充
    }
}
