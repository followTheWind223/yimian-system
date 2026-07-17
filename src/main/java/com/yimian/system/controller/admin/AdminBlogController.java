package com.yimian.system.controller.admin;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.service.BlogService;
import com.yimian.system.vo.BlogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Blog", description = "Admin blog list, status and delete APIs")
@RestController
@RequestMapping("/admin/blogs")
@RequiredArgsConstructor
public class AdminBlogController {

    private final BlogService blogService;

    @Operation(summary = "Admin blog list")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('blog:list')")
    public Result<PageInfo<BlogVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(blogService.adminList(page, size, keyword, status));
    }

    @OperationLog(module = "BLOG", operation = "ADMIN_UPDATE_BLOG_STATUS", description = "blogId=#{#id}, status=#{#status}")
    @Operation(summary = "Admin update blog status")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('blog:edit')")
    public Result<BlogVO> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(blogService.adminUpdateStatus(id, status));
    }

    @OperationLog(module = "BLOG", operation = "ADMIN_DELETE_BLOG", description = "blogId=#{#id}")
    @Operation(summary = "Admin delete blog")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('blog:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        blogService.adminDelete(id);
        return Result.success();
    }
}
