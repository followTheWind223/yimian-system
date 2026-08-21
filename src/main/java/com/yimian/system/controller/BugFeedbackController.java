package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.BugFeedbackCreateDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.BugFeedbackService;
import com.yimian.system.vo.BugFeedbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "问题反馈", description = "用户提交服务器 Bug 和问题反馈")
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class BugFeedbackController {

    private final BugFeedbackService bugFeedbackService;

    @OperationLog(module = "FEEDBACK", operation = "提交问题反馈", description = "提交问题反馈: #{#dto.title}")
    @Operation(summary = "提交问题反馈")
    @PostMapping
    public Result<BugFeedbackVO> create(@Valid @RequestBody BugFeedbackCreateDto dto) {
        return Result.success(bugFeedbackService.create(dto, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
