package com.yimian.system.controller;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.CommentCreateDto;
import com.yimian.system.dto.CommentQueryDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.CommentService;
import com.yimian.system.vo.CommentLikeVO;
import com.yimian.system.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment module", description = "Comments, replies and comment likes")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Comment list")
    @GetMapping
    @PreAuthorize("hasAuthority('comment:view')")
    public Result<PageInfo<CommentVO>> list(@Valid CommentQueryDto query) {
        return Result.success(commentService.list(query, getCurrentUserId()));
    }

    @OperationLog(module = "COMMENT", operation = "Create comment", description = "Create comment")
    @Operation(summary = "Create comment or reply")
    @PostMapping
    @PreAuthorize("hasAuthority('comment:create')")
    public Result<CommentVO> create(@Valid @RequestBody CommentCreateDto dto) {
        return Result.success(commentService.create(dto, getCurrentUserId()));
    }

    @OperationLog(module = "COMMENT", operation = "Toggle comment like", description = "Toggle comment like ID=#{#id}")
    @Operation(summary = "Like or unlike comment")
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAuthority('comment:like')")
    public Result<CommentLikeVO> toggleLike(@PathVariable Long id) {
        return Result.success(commentService.toggleLike(id, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Cannot resolve current user");
    }
}
