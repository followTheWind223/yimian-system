package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.ChangePasswordDto;
import com.yimian.system.dto.ProfileUpdateDto;
import com.yimian.system.service.BlogService;
import com.yimian.system.service.KnowledgeService;
import com.yimian.system.service.UserService;
import com.yimian.system.vo.BlogVO;
import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心控制器（当前登录用户的操作）
 */
@Tag(name = "个人中心", description = "修改个人信息、修改密码")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KnowledgeService knowledgeService;
    private final BlogService blogService;

    @Operation(summary = "查询个人信息")
    @GetMapping("/profile")
    public Result<UserVO> profile() {
        Long userId = getCurrentUserId();
        return Result.success(userService.getUserById(userId));
    }

    @OperationLog(module = "PROFILE", operation = "修改个人信息", description = "修改个人信息")
    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody ProfileUpdateDto dto) {
        Long userId = getCurrentUserId();
        return Result.success(userService.updateProfile(userId, dto));
    }

    @OperationLog(module = "PROFILE", operation = "修改密码", description = "修改密码")
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        Long userId = getCurrentUserId();
        userService.changePassword(userId, dto);
        return Result.success();
    }

    // ==================== 工具方法 ====================

    @Operation(summary = "查看用户公开资料")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view-public')")
    public Result<UserVO> publicProfile(@PathVariable Long id) {
        return Result.success(userService.getPublicUserById(id, getCurrentUserId()));
    }

    @OperationLog(module = "USER", operation = "关注/取消关注用户", description = "关注/取消关注用户 ID=#{#id}")
    @Operation(summary = "关注/取消关注用户")
    @PostMapping("/{id}/follow")
    @PreAuthorize("hasAuthority('user:follow')")
    public Result<UserVO> toggleFollow(@PathVariable Long id) {
        return Result.success(userService.toggleFollow(id, getCurrentUserId()));
    }

    @Operation(summary = "查看用户关注列表")
    @GetMapping("/{id}/following")
    @PreAuthorize("hasAuthority('user:view-public')")
    public Result<PageInfo<UserVO>> following(@PathVariable Long id,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return Result.success(userService.listFollowing(id, getCurrentUserId(), page, size));
    }

    @Operation(summary = "查看用户粉丝列表")
    @GetMapping("/{id}/followers")
    @PreAuthorize("hasAuthority('user:view-public')")
    public Result<PageInfo<UserVO>> followers(@PathVariable Long id,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return Result.success(userService.listFollowers(id, getCurrentUserId(), page, size));
    }

    @Operation(summary = "查看用户公开题目列表")
    @GetMapping("/{id}/knowledges")
    @PreAuthorize("hasAuthority('user:view-public')")
    public Result<PageInfo<KnowledgeVO>> knowledges(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        userService.getPublicUserById(id, getCurrentUserId());
        return Result.success(knowledgeService.publicListByUser(page, size, id));
    }

    @Operation(summary = "查看用户公开博客列表")
    @GetMapping("/{id}/blogs")
    @PreAuthorize("hasAuthority('user:view-public')")
    public Result<PageInfo<BlogVO>> blogs(@PathVariable Long id,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        userService.getPublicUserById(id, getCurrentUserId());
        return Result.success(blogService.publicListByUser(page, size, id));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof com.yimian.system.security.JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
