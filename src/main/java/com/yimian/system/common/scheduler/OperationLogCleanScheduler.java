package com.yimian.system.common.scheduler;

import com.yimian.system.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 操作日志定时清理
 * 每周日凌晨 3:00 清理 180 天前的日志
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogCleanScheduler {

    /** 日志保留天数 */
    private static final int RETAIN_DAYS = 180;

    private final OperationLogService operationLogService;

    @Scheduled(cron = "0 0 3 ? * SUN")
    public void cleanOldLogs() {
        log.info("========== 开始定时清理操作日志 ==========");
        try {
            int deleted = operationLogService.cleanOldLogs(RETAIN_DAYS);
            log.info("========== 操作日志清理完成, 删除 {} 条 ==========", deleted);
        } catch (Exception e) {
            log.error("操作日志清理失败", e);
        }
    }
}
