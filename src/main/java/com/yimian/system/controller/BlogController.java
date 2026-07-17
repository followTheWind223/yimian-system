package com.yimian.system.controller;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.BlogCreateDto;
import com.yimian.system.dto.BlogQueryDto;
import com.yimian.system.dto.BlogUpdateDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.BlogService;
import com.yimian.system.vo.BlogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "博客模块", description = "博客发布、编辑、列表和详情")
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @Operation(summary = "博客列表")
    @GetMapping
    @PreAuthorize("hasAuthority('blog:list')")
    public Result<PageInfo<BlogVO>> list(@Valid BlogQueryDto query) {
        return Result.success(blogService.list(query));
    }

    @Operation(summary = "我的博客")
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('blog:list')")
    public Result<PageInfo<BlogVO>> myList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Integer status) {
        return Result.success(blogService.myList(page, size, status, getCurrentUserId()));
    }

    @OperationLog(module = "BLOG", operation = "发布博客", description = "发布博客: #{#dto.title}")
    @Operation(summary = "发布博客/保存草稿")
    @PostMapping
    @PreAuthorize("hasAuthority('blog:create')")
    public Result<BlogVO> create(@Valid @RequestBody BlogCreateDto dto) {
        return Result.success(blogService.create(dto, getCurrentUserId()));
    }

    @OperationLog(module = "BLOG", operation = "编辑博客", description = "编辑博客 ID=#{#id}")
    @Operation(summary = "编辑博客")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:edit')")
    public Result<BlogVO> update(@PathVariable Long id, @Valid @RequestBody BlogUpdateDto dto) {
        return Result.success(blogService.update(id, dto, getCurrentUserId()));
    }

    @OperationLog(module = "BLOG", operation = "删除博客", description = "删除博客 ID=#{#id}")
    @Operation(summary = "删除博客")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        blogService.delete(id, getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "博客详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:view')")
    public Result<BlogVO> detail(@PathVariable Long id) {
        return Result.success(blogService.getDetail(id, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }

    @OperationLog(module = "BLOG", operation = "点赞", description = "点赞/取消点赞 博客 ID=#{#id}")
    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAuthority('blog:like')")
    public Result<Integer> toggleLike(@PathVariable Long id) {
        return Result.success(blogService.toggleLike(id, getCurrentUserId()));
    }

    @OperationLog(module = "BLOG", operation = "收藏博客", description = "收藏博客 ID=#{#id} 到收藏夹 ID=#{#folderId}")
    @Operation(summary = "收藏博客到收藏夹")
    @PostMapping("/{id}/collect")
    @PreAuthorize("hasAuthority('blog:collect')")
    public Result<Integer> collect(@PathVariable Long id, @RequestParam Long folderId) {
        return Result.success(blogService.collect(id, folderId, getCurrentUserId()));
    }
}
