package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yimian.system.common.cache.LogModuleCache;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.LogModuleCreateDto;
import com.yimian.system.dto.LogModuleUpdateDto;
import com.yimian.system.entity.LogModule;
import com.yimian.system.mapper.LogModuleMapper;
import com.yimian.system.service.LogModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogModuleServiceImpl implements LogModuleService {

    private final LogModuleMapper logModuleMapper;
    private final LogModuleCache logModuleCache;

    /**
     * 在事务提交成功后刷新缓存，避免事务回滚后缓存被污染；
     * 无事务上下文（理论上不会发生）时直接刷新。
     */
    private void refreshCacheAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    logModuleCache.refresh();
                }
            });
        } else {
            logModuleCache.refresh();
        }
    }

    @Override
    public List<LogModule> list() {
        return logModuleMapper.selectList(
                new LambdaQueryWrapper<LogModule>().orderByAsc(LogModule::getSort));
    }

    @Override
    public List<LogModule> listEnabled() {
        return logModuleMapper.selectList(
                new LambdaQueryWrapper<LogModule>()
                        .eq(LogModule::getEnabled, 1)
                        .orderByAsc(LogModule::getSort));
    }

    @Override
    public LogModule getById(Long id) {
        LogModule m = logModuleMapper.selectById(id);
        if (m == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return m;
    }

    @Override
    @Transactional
    public LogModule create(LogModuleCreateDto dto) {
        LogModule exist = logModuleMapper.selectByCode(dto.getCode());
        if (exist != null) {
            throw new BusinessException(1030, "模块编码已存在");
        }
        LogModule m = new LogModule();
        BeanUtils.copyProperties(dto, m);
        if (m.getSort() == null) m.setSort(0);
        if (m.getEnabled() == null) m.setEnabled(1);
        logModuleMapper.insert(m);
        log.info("日志模块字典创建成功: {} ({})", m.getCode(), m.getName());
        refreshCacheAfterCommit();
        return m;
    }

    @Override
    @Transactional
    public LogModule update(Long id, LogModuleUpdateDto dto) {
        LogModule m = getById(id);
        if (dto.getName() != null) m.setName(dto.getName());
        if (dto.getDescription() != null) m.setDescription(dto.getDescription());
        if (dto.getSort() != null) m.setSort(dto.getSort());
        if (dto.getEnabled() != null) m.setEnabled(dto.getEnabled());
        logModuleMapper.updateById(m);
        log.info("日志模块字典更新成功: id={}", id);
        refreshCacheAfterCommit();
        return m;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LogModule m = getById(id);
        logModuleMapper.deleteById(id);
        log.info("日志模块字典删除成功: id={} code={}", id, m.getCode());
        refreshCacheAfterCommit();
    }
}
