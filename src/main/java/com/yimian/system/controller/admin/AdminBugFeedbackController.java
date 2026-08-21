package com.yimian.system.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.BugFeedbackQueryDto;
import com.yimian.system.service.BugFeedbackService;
import com.yimian.system.vo.BugFeedbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "反馈管理", description = "管理员查看用户问题反馈")
@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminBugFeedbackController {

    private final BugFeedbackService bugFeedbackService;

    @Operation(summary = "反馈列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageInfo<BugFeedbackVO>> list(BugFeedbackQueryDto query) {
        return Result.success(bugFeedbackService.list(query));
    }

    @Operation(summary = "反馈详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<BugFeedbackVO> detail(@PathVariable Long id) {
        return Result.success(bugFeedbackService.detail(id));
    }

    @OperationLog(module = "FEEDBACK", operation = "更新反馈状态", description = "更新反馈 ID=#{#id} 状态为 #{#status}")
    @Operation(summary = "更新反馈状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<BugFeedbackVO> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(bugFeedbackService.updateStatus(id, status));
    }
}
