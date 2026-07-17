package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.service.TopicService;
import com.yimian.system.vo.TopicVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "话题模块", description = "话题列表与管理")
@RestController
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "话题列表（公开）")
    @GetMapping("/topics")
    public Result<List<TopicVO>> list(@RequestParam(required = false) String keyword) {
        return Result.success(topicService.list(keyword));
    }

    @OperationLog(module = "TOPIC", operation = "新增话题", description = "新增话题: #{#name}")
    @Operation(summary = "新增话题（管理员）")
    @PostMapping("/admin/topics")
    @PreAuthorize("hasAuthority('topic:create')")
    public Result<TopicVO> create(@RequestParam String name,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String color) {
        return Result.success(topicService.create(name, description, color));
    }

    @OperationLog(module = "TOPIC", operation = "编辑话题", description = "编辑话题 ID=#{#id}")
    @Operation(summary = "编辑话题（管理员）")
    @PutMapping("/admin/topics/{id}")
    @PreAuthorize("hasAuthority('topic:edit')")
    public Result<TopicVO> update(@PathVariable Long id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String color,
                                   @RequestParam(required = false) Integer sort) {
        return Result.success(topicService.update(id, name, description, color, sort));
    }

    @OperationLog(module = "TOPIC", operation = "删除话题", description = "删除话题 ID=#{#id}")
    @Operation(summary = "删除话题（管理员）")
    @DeleteMapping("/admin/topics/{id}")
    @PreAuthorize("hasAuthority('topic:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        topicService.delete(id);
        return Result.success();
    }
}
