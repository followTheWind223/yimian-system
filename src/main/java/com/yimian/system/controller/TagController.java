package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.service.TagService;
import com.yimian.system.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@Tag(name = "标签模块", description = "标签列表、管理员管理标签")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // ==================== 公开 ====================

    @Operation(summary = "标签列表（公开）")
    @GetMapping("/tags")
    public Result<List<TagVO>> list(@RequestParam(required = false) String keyword) {
        return Result.success(tagService.list(keyword));
    }

    // ==================== 管理员 ====================

    @OperationLog(module = "TAG", operation = "新增标签", description = "新增标签: #{#entity.name}")
    @Operation(summary = "新增标签（管理员）")
    @PostMapping("/admin/tags")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<TagVO> create(@Valid @RequestBody com.yimian.system.entity.Tag entity) {
        return Result.success(tagService.create(entity));
    }

    @OperationLog(module = "TAG", operation = "编辑标签", description = "编辑标签 ID=#{#id}")
    @Operation(summary = "编辑标签（管理员）")
    @PutMapping("/admin/tags/{id}")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<TagVO> update(@PathVariable Long id, @Valid @RequestBody com.yimian.system.entity.Tag entity) {
        return Result.success(tagService.update(id, entity));
    }

    @OperationLog(module = "TAG", operation = "删除标签", description = "删除标签 ID=#{#id}")
    @Operation(summary = "删除标签（管理员）")
    @DeleteMapping("/admin/tags/{id}")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }
}
