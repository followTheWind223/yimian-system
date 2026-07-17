package com.yimian.system.controller.admin;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.LogModuleCreateDto;
import com.yimian.system.dto.LogModuleUpdateDto;
import com.yimian.system.entity.LogModule;
import com.yimian.system.service.LogModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "日志模块字典", description = "维护操作日志的模块字典（编码→中文名）")
@RestController
@RequestMapping("/admin/logs/modules")
@RequiredArgsConstructor
public class AdminLogModuleController {

    private final LogModuleService logModuleService;

    @Operation(summary = "模块字典列表（管理用）")
    @GetMapping
    @PreAuthorize("hasAuthority('log:module:list')")
    public Result<List<LogModule>> list() {
        return Result.success(logModuleService.list());
    }

    @Operation(summary = "启用的模块（下拉框用）")
    @GetMapping("/enabled")
    public Result<List<LogModule>> listEnabled() {
        return Result.success(logModuleService.listEnabled());
    }

    @Operation(summary = "模块详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:module:list')")
    public Result<LogModule> getById(@PathVariable Long id) {
        return Result.success(logModuleService.getById(id));
    }

    @OperationLog(module = "LOG_MODULE", operation = "新增模块", description = "新增模块 #{#dto.code}")
    @Operation(summary = "新增模块")
    @PostMapping
    @PreAuthorize("hasAuthority('log:module:edit')")
    public Result<LogModule> create(@Valid @RequestBody LogModuleCreateDto dto) {
        return Result.success(logModuleService.create(dto));
    }

    @OperationLog(module = "LOG_MODULE", operation = "编辑模块", description = "编辑模块 ID=#{#id}")
    @Operation(summary = "编辑模块")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('log:module:edit')")
    public Result<LogModule> update(@PathVariable Long id, @Valid @RequestBody LogModuleUpdateDto dto) {
        return Result.success(logModuleService.update(id, dto));
    }

    @OperationLog(module = "LOG_MODULE", operation = "删除模块", description = "删除模块 ID=#{#id}")
    @Operation(summary = "删除模块")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('log:module:edit')")
    public Result<Void> delete(@PathVariable Long id) {
        logModuleService.delete(id);
        return Result.success();
    }
}
