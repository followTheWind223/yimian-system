package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.cache.LogModuleCache;
import com.yimian.system.entity.OperationLogRecord;
import com.yimian.system.mapper.OperationLogMapper;
import com.yimian.system.service.OperationLogService;
import com.yimian.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final LogModuleCache logModuleCache;

    @Override
    @Async
    public void save(OperationLogRecord log) {
        // createdAt 由 MySQL DEFAULT CURRENT_TIMESTAMP 自动填充，无需 Java 设值
        operationLogMapper.insert(log);
    }

    @Override
    public PageInfo<OperationLogVO> listLogs(int page, int size,
                                              String username, String module, Integer status,
                                              String startDate, String endDate) {
        LambdaQueryWrapper<OperationLogRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLogRecord::getUsername, username);
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLogRecord::getModule, module);
        }
        if (status != null) {
            wrapper.eq(OperationLogRecord::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(OperationLogRecord::getCreatedAt, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(OperationLogRecord::getCreatedAt,
                    LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }

        wrapper.orderByDesc(OperationLogRecord::getCreatedAt);

        PageHelper.startPage(page, size);
        List<OperationLogRecord> records = operationLogMapper.selectList(wrapper);

        List<OperationLogVO> voList = records.stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageInfo<>(voList);
    }

    @Override
    public int cleanOldLogs(int retainDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retainDays);
        int deleted = operationLogMapper.deleteOlderThan(cutoffDate);
        if (deleted > 0) {
            log.info("操作日志清理完成: 删除 {} 条 {} 天前的记录", deleted, retainDays);
        }
        return deleted;
    }

    private OperationLogVO toVO(OperationLogRecord record) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(record, vo);
        vo.setModuleName(logModuleCache.getModuleName(record.getModule()));
        return vo;
    }
}
