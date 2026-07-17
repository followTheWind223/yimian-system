package com.yimian.system.controller.admin;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.RoleCreateDto;
import com.yimian.system.dto.RolePermissionAssignDto;
import com.yimian.system.dto.RoleUpdateDto;
import com.yimian.system.entity.Role;
import com.yimian.system.service.RoleService;
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

@Tag(name = "角色管理", description = "管理员角色 CRUD")
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @OperationLog(module = "ROLE", operation = "新增角色", description = "新增角色 #{#dto.roleCode}")
    @Operation(summary = "新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public Result<Role> create(@Valid @RequestBody RoleCreateDto dto) {
        return Result.success(roleService.create(dto));
    }

    @OperationLog(module = "ROLE", operation = "编辑角色", description = "编辑角色 ID=#{#id}")
    @Operation(summary = "编辑角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Role> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateDto dto) {
        return Result.success(roleService.update(id, dto));
    }

    @OperationLog(module = "ROLE", operation = "设置角色状态", description = "设置角色 ID=#{#id} enabled=#{#enabled}")
    @Operation(summary = "启用/关闭角色")
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Role> setEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        return Result.success(roleService.setEnabled(id, Boolean.TRUE.equals(enabled)));
    }

    @OperationLog(module = "ROLE", operation = "关闭角色", description = "关闭角色 ID=#{#id}")
    @Operation(summary = "关闭角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询角色权限")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<List<String>> getPermissions(@PathVariable Long id) {
        return Result.success(roleService.getPermCodes(id));
    }

    @OperationLog(module = "ROLE", operation = "分配权限", description = "为角色 ID=#{#id} 分配权限")
    @Operation(summary = "分配权限")
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:assign-perms')")
    public Result<Void> assignPermissions(@PathVariable Long id,
                                          @Valid @RequestBody RolePermissionAssignDto dto) {
        roleService.assignPermissions(id, dto);
        return Result.success();
    }
}
