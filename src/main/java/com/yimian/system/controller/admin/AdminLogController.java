package com.yimian.system.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.result.Result;
import com.yimian.system.service.OperationLogService;
import com.yimian.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志管理
 */
@Tag(name = "日志管理", description = "管理员查看操作日志")
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "操作日志列表")
    @GetMapping
    @PreAuthorize("hasAuthority('log:list')")
    public Result<PageInfo<OperationLogVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(operationLogService.listLogs(
                page, size, username, module, status, startDate, endDate));
    }
}
