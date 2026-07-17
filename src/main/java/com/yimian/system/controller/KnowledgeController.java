package com.yimian.system.controller;

import com.github.pagehelper.PageInfo;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.KnowledgeCreateDto;
import com.yimian.system.dto.KnowledgeQueryDto;
import com.yimian.system.dto.KnowledgeUpdateDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.HotDataService;
import com.yimian.system.service.KnowledgeService;
import com.yimian.system.vo.KnowledgeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 知识题目控制器
 */
@Tag(name = "知识模块", description = "知识题目上传、编辑、查询、删除")
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final HotDataService hotDataService;

    // ==================== 直接上传（跳过审核，需要权限） ====================

    @OperationLog(module = "KNOWLEDGE", operation = "直接上传题目", description = "直接上传题目（跳过审核）: #{#dto.title}")
    @Operation(summary = "直接上传题目（跳过审核）")
    @PostMapping("/direct")
    @PreAuthorize("hasAuthority('knowledge:direct-upload')")
    public Result<KnowledgeVO> createDirect(@Valid @RequestBody KnowledgeCreateDto dto) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.createDirect(dto, userId));
    }

    // ==================== 提交审核 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "提交题目", description = "提交知识题目: #{#dto.title}")
    @Operation(summary = "提交题目（受审核开关控制）")
    @PostMapping("/submit")
    public Result<KnowledgeVO> submit(@Valid @RequestBody KnowledgeCreateDto dto) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.submit(dto, userId));
    }

    // ==================== 编辑 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "编辑题目", description = "编辑知识题目 ID=#{#id}")
    @Operation(summary = "编辑题目")
    @PutMapping("/{id}")
    public Result<KnowledgeVO> update(@PathVariable Long id,
                                       @Valid @RequestBody KnowledgeUpdateDto dto) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.update(id, dto, userId));
    }

    // ==================== 删除 ====================

    @OperationLog(module = "KNOWLEDGE", operation = "删除题目", description = "删除知识题目 ID=#{#id}")
    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        knowledgeService.delete(id, userId);
        return Result.success();
    }

    // ==================== 列表查询 ====================

    @Operation(summary = "题目列表（分页）")
    @GetMapping
    public Result<PageInfo<KnowledgeVO>> list(@Valid KnowledgeQueryDto query) {
        return Result.success(knowledgeService.list(query));
    }

    // ==================== 热门题目 ====================

    @Operation(summary = "今日热门题目")
    @GetMapping("/hot/today")
    public Result<java.util.List<KnowledgeVO>> todayHot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(hotDataService.getTodayHotKnowledge(limit));
    }

    @Operation(summary = "本周热门题目")
    @GetMapping("/hot/weekly")
    public Result<java.util.List<KnowledgeVO>> weeklyHot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(hotDataService.getWeeklyHotKnowledge(limit));
    }

    // ==================== 详情查询 ====================

    @Operation(summary = "题目详情")
    @GetMapping("/{id}")
    public Result<KnowledgeVO> getById(@PathVariable Long id) {
        return Result.success(knowledgeService.getById(id, getCurrentUserIdOrNull()));
    }

    @OperationLog(module = "KNOWLEDGE", operation = "鐐硅禐棰樼洰", description = "鐐硅禐/鍙栨秷鐐硅禐 棰樼洰 ID=#{#id}")
    @Operation(summary = "鐐硅禐/鍙栨秷鐐硅禐棰樼洰")
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAuthority('knowledge:like')")
    public Result<Integer> toggleLike(@PathVariable Long id) {
        return Result.success(knowledgeService.toggleLike(id, getCurrentUserId()));
    }

    // ==================== 去重检查 ====================

    @Operation(summary = "检查内容是否重复")
    @GetMapping("/check-hash")
    public Result<Boolean> checkHash(@RequestParam String hash) {
        return Result.success(knowledgeService.isContentDuplicate(hash));
    }

    // ==================== 我的题目 ====================

    @Operation(summary = "我的题目列表（分页）")
    @GetMapping("/my")
    public Result<PageInfo<KnowledgeVO>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.myList(page, size, status, userId));
    }

    // ==================== 审核开关（管理员） ====================

    @OperationLog(module = "KNOWLEDGE", operation = "审核开关", description = "修改审核开关为: #{#enabled}")
    @Operation(summary = "设置审核开关（管理员，运行时生效）")
    @PutMapping("/audit-switch")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<Boolean> setAuditSwitch(@RequestParam boolean enabled) {
        knowledgeService.setAuditEnabled(enabled);
        return Result.success(enabled);
    }

    @Operation(summary = "查询审核开关状态")
    @GetMapping("/audit-switch")
    @PreAuthorize("hasAuthority('knowledge:audit')")
    public Result<Boolean> getAuditSwitch() {
        return Result.success(knowledgeService.getAuditEnabled());
    }

    // ==================== 工具方法 ====================

    private Long getCurrentUserIdOrNull() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        return null;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
