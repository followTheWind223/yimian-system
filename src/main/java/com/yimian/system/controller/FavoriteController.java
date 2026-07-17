package com.yimian.system.controller;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.FavoriteFolderCreateDto;
import com.yimian.system.dto.FavoriteFolderUpdateDto;
import com.yimian.system.dto.FavoriteItemCreateDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.FavoriteService;
import com.yimian.system.vo.FavoriteCheckVO;
import com.yimian.system.vo.FavoriteFolderDetailVO;
import com.yimian.system.vo.FavoriteFolderVO;
import com.yimian.system.vo.FavoriteItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收藏夹模块", description = "用户收藏夹创建、管理和收藏题目")
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @OperationLog(module = "FAVORITE", operation = "创建收藏夹", description = "创建收藏夹: #{#dto.name}")
    @Operation(summary = "创建收藏夹")
    @PostMapping("/folders")
    @PreAuthorize("hasAuthority('favorite:create')")
    public Result<FavoriteFolderVO> createFolder(@Valid @RequestBody FavoriteFolderCreateDto dto) {
        return Result.success(favoriteService.createFolder(dto, getCurrentUserId()));
    }

    @OperationLog(module = "FAVORITE", operation = "编辑收藏夹", description = "编辑收藏夹 ID=#{#id}")
    @Operation(summary = "编辑收藏夹")
    @PutMapping("/folders/{id}")
    @PreAuthorize("hasAuthority('favorite:edit')")
    public Result<FavoriteFolderVO> updateFolder(@PathVariable Long id,
                                                 @Valid @RequestBody FavoriteFolderUpdateDto dto) {
        return Result.success(favoriteService.updateFolder(id, dto, getCurrentUserId()));
    }

    @OperationLog(module = "FAVORITE", operation = "删除收藏夹", description = "删除收藏夹 ID=#{#id}")
    @Operation(summary = "删除收藏夹")
    @DeleteMapping("/folders/{id}")
    @PreAuthorize("hasAuthority('favorite:delete')")
    public Result<Void> deleteFolder(@PathVariable Long id) {
        favoriteService.deleteFolder(id, getCurrentUserId());
        return Result.success();
    }

    @OperationLog(module = "FAVORITE", operation = "收藏题目", description = "收藏题目到收藏夹 ID=#{#folderId}")
    @Operation(summary = "收藏知识题目")
    @PostMapping("/folders/{folderId}/items")
    @PreAuthorize("hasAuthority('favorite:item:add')")
    public Result<FavoriteItemVO> addItem(@PathVariable Long folderId,
                                          @Valid @RequestBody FavoriteItemCreateDto dto) {
        return Result.success(favoriteService.addItem(folderId, dto, getCurrentUserId()));
    }

    @Operation(summary = "收藏夹条目列表/搜索")
    @GetMapping("/folders/{folderId}/items")
    @PreAuthorize("hasAuthority('favorite:view')")
    public Result<PageInfo<FavoriteItemVO>> listFolderItems(@PathVariable Long folderId,
                                                            @RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            @RequestParam(required = false) String keyword) {
        return Result.success(favoriteService.listFolderItems(folderId, page, size, keyword, getCurrentUserId()));
    }

    @OperationLog(module = "FAVORITE", operation = "取消收藏", description = "移除收藏条目 ID=#{#itemId}")
    @Operation(summary = "取消收藏")
    @DeleteMapping("/folders/{folderId}/items/{itemId}")
    @PreAuthorize("hasAuthority('favorite:item:delete')")
    public Result<Void> removeItem(@PathVariable Long folderId, @PathVariable Long itemId) {
        favoriteService.removeItem(folderId, itemId, getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "用户公开收藏夹列表")
    @GetMapping("/folders")
    @PreAuthorize("hasAuthority('favorite:list')")
    public Result<List<FavoriteFolderVO>> listPublicFolders(@RequestParam Long userId) {
        return Result.success(favoriteService.listPublicFolders(userId));
    }

    @Operation(summary = "我的收藏夹列表")
    @GetMapping("/my-folders")
    @PreAuthorize("hasAuthority('favorite:list')")
    public Result<List<FavoriteFolderVO>> listMyFolders() {
        return Result.success(favoriteService.listMyFolders(getCurrentUserId()));
    }

    @Operation(summary = "收藏夹详情")
    @GetMapping("/folders/{id}")
    @PreAuthorize("hasAuthority('favorite:view')")
    public Result<FavoriteFolderDetailVO> getFolderDetail(@PathVariable Long id,
                                                          @RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size,
                                                          @RequestParam(required = false) String keyword) {
        return Result.success(favoriteService.getFolderDetail(id, page, size, keyword, getCurrentUserId()));
    }

    @Operation(summary = "检查题目是否已收藏")
    @GetMapping("/check")
    @PreAuthorize("hasAuthority('favorite:view')")
    public Result<FavoriteCheckVO> checkCollected(@RequestParam Long knowledgeId) {
        return Result.success(favoriteService.checkCollected(knowledgeId, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
