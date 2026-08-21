package com.yimian.system.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.SystemAnnouncementCreateDto;
import com.yimian.system.dto.SystemAnnouncementQueryDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.SystemAnnouncementService;
import com.yimian.system.vo.SystemAnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统公告管理", description = "管理员发布系统公告")
@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AdminSystemAnnouncementController {

    private final SystemAnnouncementService announcementService;

    @Operation(summary = "公告列表")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('announcement:list')")
    public Result<PageInfo<SystemAnnouncementVO>> list(SystemAnnouncementQueryDto query) {
        return Result.success(announcementService.list(query));
    }

    @OperationLog(module = "ANNOUNCEMENT", operation = "发布系统公告", description = "发布系统公告 #{#dto.title}")
    @Operation(summary = "发布系统公告")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('announcement:create')")
    public Result<SystemAnnouncementVO> publish(@Valid @RequestBody SystemAnnouncementCreateDto dto) {
        return Result.success(announcementService.publish(dto, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Cannot resolve current user");
    }
}
