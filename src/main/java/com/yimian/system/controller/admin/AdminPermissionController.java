package com.yimian.system.controller.admin;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.PermissionCreateDto;
import com.yimian.system.dto.PermissionUpdateDto;
import com.yimian.system.entity.Permission;
import com.yimian.system.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "权限管理", description = "管理员权限 CRUD")
@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "权限列表")
    @GetMapping
    @PreAuthorize("hasAuthority('perm:list')")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    @Operation(summary = "权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perm:view')")
    public Result<Permission> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @OperationLog(module = "PERMISSION", operation = "新增权限", description = "新增权限 #{#dto.permCode}")
    @Operation(summary = "新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('perm:create')")
    public Result<Permission> create(@Valid @RequestBody PermissionCreateDto dto) {
        return Result.success(permissionService.create(dto));
    }

    @OperationLog(module = "PERMISSION", operation = "编辑权限", description = "编辑权限 ID=#{#id}")
    @Operation(summary = "编辑权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perm:edit')")
    public Result<Permission> update(@PathVariable Long id, @Valid @RequestBody PermissionUpdateDto dto) {
        return Result.success(permissionService.update(id, dto));
    }

    @OperationLog(module = "PERMISSION", operation = "设置权限状态", description = "设置权限 ID=#{#id} enabled=#{#enabled}")
    @Operation(summary = "启用/关闭权限")
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('perm:delete')")
    public Result<Permission> setEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        return Result.success(permissionService.setEnabled(id, Boolean.TRUE.equals(enabled)));
    }

    @OperationLog(module = "PERMISSION", operation = "关闭权限", description = "关闭权限 ID=#{#id}")
    @Operation(summary = "关闭权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perm:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }
}
