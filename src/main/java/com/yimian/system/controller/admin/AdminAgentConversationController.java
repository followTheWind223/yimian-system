package com.yimian.system.controller.admin;

import com.yimian.system.common.result.Result;
import com.yimian.system.service.AgentConversationAuditService;
import com.yimian.system.vo.AgentConversationDetailVO;
import com.yimian.system.vo.AgentConversationSessionPageVO;
import com.yimian.system.vo.AgentConversationStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent conversation audit", description = "Read-only Agent conversation and token audit")
@Validated
@RestController
@RequestMapping("/admin/agent/conversations")
@RequiredArgsConstructor
public class AdminAgentConversationController {

    private final AgentConversationAuditService auditService;

    @Operation(summary = "Agent conversation overview")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('agent:conversation:list')")
    public Result<AgentConversationStatsVO> stats() {
        return Result.success(auditService.getStats());
    }

    @Operation(summary = "Agent conversation list")
    @GetMapping
    @PreAuthorize("hasAuthority('agent:conversation:list')")
    public Result<AgentConversationSessionPageVO> list(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false)
            @Pattern(regexp = "support|quick", message = "sessionType must be support or quick")
            String sessionType,
            @RequestParam(required = false) @Min(0) @Max(1) Integer status) {
        return Result.success(auditService.listSessions(page, size, userId, sessionType, status));
    }

    @Operation(summary = "Agent conversation messages")
    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('agent:conversation:view')")
    public Result<AgentConversationDetailVO> detail(
            @PathVariable @Positive Long sessionId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(200) int size) {
        return Result.success(auditService.getSessionDetail(sessionId, page, size));
    }
}
