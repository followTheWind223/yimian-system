package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.OperationLogRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 操作日志 Mapper
 */
public interface OperationLogMapper extends BaseMapper<OperationLogRecord> {

    /**
     * 删除指定时间之前的日志（物理删除）
     */
    @Delete("DELETE FROM sys_operation_log WHERE created_at < #{cutoffDate}")
    int deleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
