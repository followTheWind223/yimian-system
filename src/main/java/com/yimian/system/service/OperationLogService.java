package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.entity.OperationLogRecord;
import com.yimian.system.vo.OperationLogVO;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /** 异步保存操作日志 */
    void save(OperationLogRecord log);

    /** 分页查询日志 */
    PageInfo<OperationLogVO> listLogs(int page, int size,
                                      String username, String module, Integer status,
                                      String startDate, String endDate);

    /** 清理指定天数之前的日志 */
    int cleanOldLogs(int retainDays);
}
