package com.yimian.system.controller;

import com.yimian.system.common.result.Result;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.SystemAnnouncementService;
import com.yimian.system.vo.SystemAnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统公告", description = "用户端系统公告")
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class SystemAnnouncementController {

    private final SystemAnnouncementService announcementService;

    @Operation(summary = "未读重要公告")
    @GetMapping("/important-unread")
    public Result<List<SystemAnnouncementVO>> importantUnread() {
        return Result.success(announcementService.listUnreadImportant(getCurrentUserId()));
    }

    @Operation(summary = "确认重要公告")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        announcementService.confirm(id, getCurrentUserId());
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Cannot resolve current user");
    }
}
