package com.yimian.system.controller.admin;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.service.KnowledgeService;
import com.yimian.system.vo.KnowledgeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识题目审核管理（管理员）
 */
@Tag(name = "知识审核管理", description = "管理员审核知识题目")
@RestController
@RequestMapping("/admin/knowledge")
@RequiredArgsConstructor
public class AdminKnowledgeController {

    private final KnowledgeService knowledgeService;

    // ==================== 待审核列表 ====================

    @Operation(summary = "待审核列表（管理员）")
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<?> pending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        // 查询 status=0（待审核）的题目，支持按关键词筛选
        com.yimian.system.dto.KnowledgeQueryDto query = new com.yimian.system.dto.KnowledgeQueryDto();
        query.setPage(page);
        query.setSize(size);
        query.setStatus(0);
        if (keyword != null && !keyword.isBlank()) {
            query.setKeyword(keyword);
        }
        return Result.success(knowledgeService.list(query));
    }

    // ==================== 审核通过 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "审核通过", description = "审核通过知识题目 ID=#{#id}")
    @Operation(summary = "审核通过")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<KnowledgeVO> approve(@PathVariable Long id) {
        Long auditorId = getCurrentUserId();
        KnowledgeVO vo = knowledgeService.approve(id, auditorId);
        return Result.success(vo);
    }

    // ==================== 审核拒绝 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "审核拒绝", description = "审核拒绝知识题目 ID=#{#id}")
    @Operation(summary = "审核拒绝")
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<KnowledgeVO> reject(@PathVariable Long id,
                                      @RequestParam String remark) {
        Long auditorId = getCurrentUserId();
        KnowledgeVO vo = knowledgeService.reject(id, remark, auditorId);
        return Result.success(vo);
    }

    // ==================== 批量审核 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "批量审核", description = "批量审核知识题目")
    @Operation(summary = "批量审核（通过/拒绝）")
    @PutMapping("/batch-audit")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<Integer> batchAudit(@RequestBody BatchAuditRequest request) {
        Long auditorId = getCurrentUserId();
        int count = knowledgeService.batchAudit(request.getIds(), request.getApprove(), request.getRemark(), auditorId);
        return Result.success(count);
    }

    @lombok.Data
    public static class BatchAuditRequest {
        private List<Long> ids;
        private Boolean approve;
        private String remark;
    }

    // ==================== 工具方法 ====================

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof com.yimian.system.security.JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
