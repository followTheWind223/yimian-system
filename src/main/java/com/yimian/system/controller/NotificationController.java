package com.yimian.system.controller;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.NotificationQueryDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.NotificationService;
import com.yimian.system.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification module", description = "Notification")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "list")
    @GetMapping
    public Result<PageInfo<NotificationVO>> list(NotificationQueryDto query) {
        return Result.success(notificationService.list(query, getCurrentUserId()));
    }

    @Operation(summary = "unread count")
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> unreadCount() {
        int count = notificationService.countUnread(getCurrentUserId());
        return Result.success(Map.of("count", count));
    }

    @OperationLog(module = "NOTIFICATION", operation = "Mark read", description = "mark read")
    @Operation(summary = "mark read")
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id, getCurrentUserId());
        return Result.success();
    }

    @OperationLog(module = "NOTIFICATION", operation = "Mark all read", description = "mark all read")
    @Operation(summary = "mark all read")
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return Result.success();
    }

    @OperationLog(module = "NOTIFICATION", operation = "Delete", description = "delete")
    @Operation(summary = "delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        notificationService.delete(id, getCurrentUserId());
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
