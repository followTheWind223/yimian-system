package com.yimian.system.controller.admin;

import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.Result;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.service.AgentConversationAuditService;
import com.yimian.system.vo.AgentUsageAnalyticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent statistics", description = "Aggregated Agent usage and Token statistics")
@Validated
@RestController
@RequestMapping("/admin/agent/statistics")
@RequiredArgsConstructor
public class AdminAgentStatisticsController {

    private final AgentConversationAuditService auditService;

    @Operation(summary = "Agent usage analytics")
    @GetMapping("/usage")
    @PreAuthorize("hasAuthority('agent:statistics:view')")
    public Result<AgentUsageAnalyticsVO> usage(
            @RequestParam(defaultValue = "7") @Min(1) @Max(30) int days) {
        if (days != 1 && days != 7 && days != 30) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "统计周期仅支持 1、7 或 30 天");
        }
        return Result.success(auditService.getUsageAnalytics(days));
    }
}
