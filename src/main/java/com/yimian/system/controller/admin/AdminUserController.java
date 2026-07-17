package com.yimian.system.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.AdminResetPasswordDto;
import com.yimian.system.dto.UserCreateDto;
import com.yimian.system.dto.UserRoleAssignDto;
import com.yimian.system.dto.UserUpdateDto;
import com.yimian.system.service.UserService;
import com.yimian.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "管理员用户 CRUD")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "用户列表")
    @GetMapping
    @PreAuthorize("hasAuthority('user:list')")
    public Result<PageInfo<UserVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.listUsers(page, size, keyword, role, status));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @OperationLog(module = "USER", operation = "新增用户", description = "新增用户 #{#dto.username}")
    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public Result<UserVO> create(@Valid @RequestBody UserCreateDto dto) {
        return Result.success(userService.createUser(dto));
    }

    @OperationLog(module = "USER", operation = "编辑用户", description = "编辑用户 ID=#{#id}")
    @Operation(summary = "编辑用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        return Result.success(userService.updateUser(id, dto));
    }

    @OperationLog(module = "USER", operation = "重置密码", description = "重置用户 ID=#{#id} 的密码")
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('user:reset-password')")
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody AdminResetPasswordDto dto) {
        userService.resetPassword(id, dto);
        return Result.success();
    }

    @OperationLog(module = "USER", operation = "删除用户", description = "删除用户 ID=#{#id}")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @OperationLog(module = "USER", operation = "分配角色", description = "为用户 ID=#{#id} 分配角色")
    @Operation(summary = "分配角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:assign-roles')")
    public Result<Void> assignRoles(@PathVariable Long id, @Valid @RequestBody UserRoleAssignDto dto) {
        userService.assignRoles(id, dto);
        return Result.success();
    }
}
