package com.yimian.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.KnowledgeCreateDto;
import com.yimian.system.dto.KnowledgeQueryDto;
import com.yimian.system.dto.KnowledgeUpdateDto;
import com.yimian.system.entity.Knowledge;
import com.yimian.system.entity.KnowledgeLike;
import com.yimian.system.entity.SystemSetting;
import com.yimian.system.entity.Tag;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.KnowledgeLikeMapper;
import com.yimian.system.mapper.KnowledgeMapper;
import com.yimian.system.mapper.KnowledgeTagMapper;
import com.yimian.system.mapper.SystemSettingMapper;
import com.yimian.system.mapper.TagMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.HotDataService;
import com.yimian.system.service.KnowledgeService;
import com.yimian.system.service.MentionService;
import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.KnowledgeVO.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final String AUDIT_SETTING_KEY = "knowledge.audit.enabled";

    private final KnowledgeMapper knowledgeMapper;
    private final KnowledgeLikeMapper knowledgeLikeMapper;
    private final KnowledgeTagMapper knowledgeTagMapper;
    private final TagMapper tagMapper;
    private final SystemSettingMapper systemSettingMapper;
    private final UserMapper userMapper;
    private final HotDataService hotDataService;
    private final MentionService mentionService;

    @Value("${audit.enabled:true}")
    private boolean defaultAuditEnabled;

    // ==================== 直接上传（跳过审核） ====================

    @Override
    @Transactional
    public KnowledgeVO createDirect(KnowledgeCreateDto dto, Long userId) {
        String hash = sha256(dto.getContent());
        checkDuplicate(hash);

        Knowledge entity = buildEntity(dto, userId);
        entity.setContentHash(hash);
        entity.setStatus(1);
        knowledgeMapper.insert(entity);

        bindTags(entity.getId(), dto.getTagIds());
        notifyPublishedMentions(entity);

        log.info("直接上传知识题目成功: id={}, title={}, userId={}", entity.getId(), dto.getTitle(), userId);
        return buildVO(entity);
    }

    // ==================== 提交审核 ====================

    @Override
    @Transactional
    public KnowledgeVO submit(KnowledgeCreateDto dto, Long userId) {
        String hash = sha256(dto.getContent());
        checkDuplicate(hash);

        Knowledge entity = buildEntity(dto, userId);
        entity.setContentHash(hash);
        boolean auditEnabled = getAuditEnabled();
        entity.setStatus(auditEnabled ? 0 : 1);

        knowledgeMapper.insert(entity);
        bindTags(entity.getId(), dto.getTagIds());
        notifyPublishedMentions(entity);

        log.info("提交知识题目: id={}, title={}, status={}, auditEnabled={}, userId={}",
                entity.getId(), dto.getTitle(), entity.getStatus(), auditEnabled, userId);
        return buildVO(entity);
    }

    // ==================== 编辑 ====================

    @Override
    @Transactional
    public KnowledgeVO update(Long id, KnowledgeUpdateDto dto, Long userId) {
        Knowledge entity = getByIdInternal(id);
        // 仅允许编辑自己提交的、且状态为草稿或审核拒绝的题目
        if (!entity.getSubmitUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (entity.getStatus() != 2 && entity.getStatus() != 3) {
            throw new BusinessException(ResultCode.KNOWLEDGE_STATUS_ERROR);
        }

        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getContent() != null) {
            entity.setContent(dto.getContent());
            entity.setContentHash(sha256(dto.getContent()));
        }
        if (dto.getDifficulty() != null) entity.setDifficulty(dto.getDifficulty());

        // 编辑后重新提交审核
        boolean auditEnabled = getAuditEnabled();
        entity.setStatus(auditEnabled ? 0 : 1);
        knowledgeMapper.updateById(entity);

        // 更新标签绑定
        if (dto.getTagIds() != null) {
            knowledgeTagMapper.deleteByKnowledgeId(id);
            bindTags(id, dto.getTagIds());
        }
        notifyPublishedMentions(entity);

        log.info("编辑知识题目成功: id={}, title={}, userId={}", id, dto.getTitle(), userId);
        return buildVO(entity);
    }

    // ==================== 删除 ====================

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Knowledge entity = getByIdInternal(id);
        if (!entity.getSubmitUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // TODO: 5.2.7 同步删除 RAG 文档
        knowledgeMapper.deleteById(id);
        log.info("删除知识题目成功: id={}, userId={}", id, userId);
    }

    // ==================== 分页列表 ====================

    @Override
    public PageInfo<KnowledgeVO> list(KnowledgeQueryDto query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<Knowledge> list = knowledgeMapper.selectPage(
                query.getKeyword(),
                query.getDifficulty(),
                query.getTagId(),
                query.getStatus());
        PageInfo<Knowledge> source = new PageInfo<>(list);
        List<KnowledgeVO> voList = list.stream().map(this::buildVO).collect(Collectors.toList());
        PageInfo<KnowledgeVO> result = copyPage(source, voList);
        Long total = knowledgeMapper.countPage(
                query.getKeyword(),
                query.getDifficulty(),
                query.getTagId(),
                query.getStatus());
        applyTotal(result, total != null ? total : source.getTotal());
        return result;
    }

    // ==================== 详情 ====================

    @Override
    public KnowledgeVO getById(Long id) {
        return getById(id, null);
    }

    // ==================== 去重检查 ====================

    @Override
    public KnowledgeVO getById(Long id, Long currentUserId) {
        Knowledge entity = getByIdInternal(id);
        knowledgeMapper.incrementViewCount(id);
        hotDataService.incrView(id);
        entity.setViewCount((entity.getViewCount() != null ? entity.getViewCount() : 0) + 1);
        return buildVO(entity, currentUserId);
    }

    @Override
    @Transactional
    public int toggleLike(Long id, Long userId) {
        Knowledge entity = getByIdInternal(id);
        if (!Integer.valueOf(1).equals(entity.getStatus())) {
            throw new BusinessException(ResultCode.KNOWLEDGE_STATUS_ERROR);
        }

        KnowledgeLike active = knowledgeLikeMapper.selectActive(id, userId);
        if (active != null) {
            knowledgeLikeMapper.softDelete(id, userId);
            knowledgeMapper.updateLikeCount(id, -1);
        } else {
            KnowledgeLike any = knowledgeLikeMapper.selectAny(id, userId);
            if (any != null) {
                knowledgeLikeMapper.restore(any.getId());
            } else {
                KnowledgeLike like = new KnowledgeLike();
                like.setKnowledgeId(id);
                like.setUserId(userId);
                like.setDeleted(0);
                knowledgeLikeMapper.insert(like);
            }
            knowledgeMapper.updateLikeCount(id, 1);
            hotDataService.incrLike(id);
        }

        Knowledge updated = knowledgeMapper.selectById(id);
        return updated != null && updated.getLikeCount() != null ? updated.getLikeCount() : 0;
    }

    @Override
    public boolean isContentDuplicate(String hash) {
        return knowledgeMapper.selectByContentHash(hash) != null;
    }

    // ==================== 我的题目 ====================

    @Override
    public PageInfo<KnowledgeVO> myList(Integer page, Integer size, Integer status, Long userId) {
        PageHelper.startPage(page, size);
        List<Knowledge> list = knowledgeMapper.selectMyPage(userId, status);
        PageInfo<Knowledge> source = new PageInfo<>(list);
        List<KnowledgeVO> voList = list.stream().map(this::buildVO).collect(Collectors.toList());
        PageInfo<KnowledgeVO> result = copyPage(source, voList);
        Long total = knowledgeMapper.countMyPage(userId, status);
        applyTotal(result, total != null ? total : source.getTotal());
        return result;
    }

    @Override
    public PageInfo<KnowledgeVO> publicListByUser(Integer page, Integer size, Long userId) {
        PageHelper.startPage(page != null && page > 0 ? page : 1, size != null && size > 0 ? size : 10);
        List<Knowledge> list = knowledgeMapper.selectMyPage(userId, 1);
        PageInfo<Knowledge> source = new PageInfo<>(list);
        List<KnowledgeVO> voList = list.stream().map(this::buildVO).collect(Collectors.toList());
        PageInfo<KnowledgeVO> result = copyPage(source, voList);
        Long total = knowledgeMapper.countMyPage(userId, 1);
        applyTotal(result, total != null ? total : source.getTotal());
        return result;
    }

    // ==================== 审核开关（系统配置） ====================

    @Override
    public boolean getAuditEnabled() {
        SystemSetting setting = findAuditSetting();
        if (setting == null) {
            setting = createDefaultAuditSetting();
        }
        return Boolean.parseBoolean(setting.getSettingValue());
    }

    @Override
    @Transactional
    public void setAuditEnabled(boolean enabled) {
        SystemSetting setting = findAuditSetting();
        if (setting == null) {
            setting = createDefaultAuditSetting();
        }
        setting.setSettingValue(Boolean.toString(enabled));
        systemSettingMapper.updateById(setting);
        log.info("审核开关已持久化变更为: {}", enabled ? "开启" : "关闭");
    }

    private SystemSetting findAuditSetting() {
        return systemSettingMapper.selectOne(new LambdaQueryWrapper<SystemSetting>()
                .eq(SystemSetting::getSettingKey, AUDIT_SETTING_KEY)
                .last("LIMIT 1"));
    }

    private synchronized SystemSetting createDefaultAuditSetting() {
        SystemSetting existing = findAuditSetting();
        if (existing != null) {
            return existing;
        }
        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(AUDIT_SETTING_KEY);
        setting.setSettingValue(Boolean.toString(defaultAuditEnabled));
        setting.setRemark("题目提交审核开关：true=提交后待审核，false=提交后直接公开");
        systemSettingMapper.insert(setting);
        return setting;
    }

    // ==================== 审核通过 ====================

    @Override
    @Transactional
    public KnowledgeVO approve(Long id, Long auditorId) {
        Knowledge entity = getByIdInternal(id);
        if (entity.getStatus() != 0) {
            throw new BusinessException(ResultCode.KNOWLEDGE_STATUS_ERROR);
        }
        entity.setStatus(1);
        entity.setAuditUserId(auditorId);
        entity.setAuditTime(java.time.LocalDateTime.now());
        knowledgeMapper.updateById(entity);
        log.info("审核通过知识题目: id={}, title={}, auditorId={}", id, entity.getTitle(), auditorId);
        notifyPublishedMentions(entity);
        // TODO: 5.3.2 异步调用 Agent RAG 入库
        return buildVO(entity);
    }

    // ==================== 审核拒绝 ====================

    @Override
    @Transactional
    public KnowledgeVO reject(Long id, String remark, Long auditorId) {
        Knowledge entity = getByIdInternal(id);
        if (entity.getStatus() != 0) {
            throw new BusinessException(ResultCode.KNOWLEDGE_STATUS_ERROR);
        }
        entity.setStatus(2);
        entity.setAuditRemark(remark);
        entity.setAuditUserId(auditorId);
        entity.setAuditTime(java.time.LocalDateTime.now());
        knowledgeMapper.updateById(entity);
        log.info("审核拒绝知识题目: id={}, title={}, remark={}, auditorId={}", id, entity.getTitle(), remark, auditorId);
        return buildVO(entity);
    }

    // ==================== 批量审核 ====================

    @Override
    @Transactional
    public int batchAudit(java.util.List<Long> ids, Boolean approve, String remark, Long auditorId) {
        int count = 0;
        for (Long id : ids) {
            if (Boolean.TRUE.equals(approve)) {
                approve(id, auditorId);
            } else {
                reject(id, remark, auditorId);
            }
            count++;
        }
        log.info("批量审核完成: ids={}, approve={}, count={}, auditorId={}", ids, approve, count, auditorId);
        return count;
    }

    // ==================== 私有方法 ====================

    private Knowledge getByIdInternal(Long id) {
        Knowledge entity = knowledgeMapper.selectById(id);
        if (entity == null || entity.getDeleted() == 1) {
            throw new BusinessException(ResultCode.KNOWLEDGE_NOT_FOUND);
        }
        return entity;
    }

    private void notifyPublishedMentions(Knowledge entity) {
        if (entity != null && Integer.valueOf(1).equals(entity.getStatus())) {
            mentionService.notifyMentions(entity.getSubmitUserId(), entity.getContent(),
                    "knowledge", entity.getId(), "knowledge", entity.getId());
        }
    }

    private void checkDuplicate(String hash) {
        if (isContentDuplicate(hash)) {
            throw new BusinessException(ResultCode.KNOWLEDGE_CONTENT_DUPLICATE);
        }
    }

    private Knowledge buildEntity(KnowledgeCreateDto dto, Long userId) {
        Knowledge entity = new Knowledge();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : 1);
        entity.setSubmitUserId(userId);
        entity.setViewCount(0);
        entity.setLikeCount(0);
        entity.setCollectCount(0);
        entity.setCommentCount(0);
        entity.setDeleted(0);
        return entity;
    }

    private void bindTags(Long knowledgeId, List<Long> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            knowledgeTagMapper.insertBatch(knowledgeId, tagIds);
        }
    }

    private KnowledgeVO buildVO(Knowledge entity) {
        return buildVO(entity, null);
    }

    private KnowledgeVO buildVO(Knowledge entity, Long currentUserId) {
        KnowledgeVO vo = new KnowledgeVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setDifficulty(entity.getDifficulty());
        vo.setStatus(entity.getStatus());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setAuditUserId(entity.getAuditUserId());
        vo.setAuditTime(entity.getAuditTime());
        vo.setSubmitUserId(entity.getSubmitUserId());
        User submitUser = findUser(entity.getSubmitUserId());
        User auditUser = findUser(entity.getAuditUserId());
        vo.setSubmitUserName(displayNameOf(submitUser, entity.getSubmitUserId()));
        vo.setSubmitUserAvatar(avatarOf(submitUser));
        vo.setAuditUserName(displayNameOf(auditUser, entity.getAuditUserId()));
        vo.setAuditUserAvatar(avatarOf(auditUser));
        vo.setViewCount(entity.getViewCount() != null ? entity.getViewCount() : 0);
        vo.setLikeCount(entity.getLikeCount() != null ? entity.getLikeCount() : 0);
        vo.setLiked(currentUserId != null && knowledgeLikeMapper.selectActive(entity.getId(), currentUserId) != null);
        vo.setCollectCount(entity.getCollectCount() != null ? entity.getCollectCount() : 0);
        vo.setCommentCount(entity.getCommentCount() != null ? entity.getCommentCount() : 0);
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        // 组装标签
        List<Long> tagIds = knowledgeTagMapper.selectTagIdsByKnowledgeId(entity.getId());
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectByIds(tagIds);
            vo.setTags(tags.stream().map(t -> {
                TagVO tv = new TagVO();
                tv.setId(t.getId());
                tv.setName(t.getName());
                tv.setColor(t.getColor());
                return tv;
            }).collect(Collectors.toList()));
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }

    // ==================== 工具方法 ====================

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    private String displayNameOf(User user, Long userId) {
        if (userId == null) {
            return null;
        }
        if (user == null) {
            return "用户#" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return "用户#" + userId;
    }

    private String avatarOf(User user) {
        return user == null ? null : user.getAvatar();
    }

    private PageInfo<KnowledgeVO> copyPage(PageInfo<Knowledge> source, List<KnowledgeVO> list) {
        PageInfo<KnowledgeVO> target = new PageInfo<>();
        target.setList(list);
        target.setTotal(source.getTotal());
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setSize(source.getSize());
        target.setStartRow(source.getStartRow());
        target.setEndRow(source.getEndRow());
        target.setPages(source.getPages());
        target.setPrePage(source.getPrePage());
        target.setNextPage(source.getNextPage());
        target.setIsFirstPage(source.isIsFirstPage());
        target.setIsLastPage(source.isIsLastPage());
        target.setHasPreviousPage(source.isHasPreviousPage());
        target.setHasNextPage(source.isHasNextPage());
        target.setNavigatePages(source.getNavigatePages());
        target.setNavigatepageNums(source.getNavigatepageNums());
        target.setNavigateFirstPage(source.getNavigateFirstPage());
        target.setNavigateLastPage(source.getNavigateLastPage());
        return target;
    }

    private void applyTotal(PageInfo<KnowledgeVO> page, long total) {
        int pageNum = page.getPageNum() > 0 ? page.getPageNum() : 1;
        int pageSize = page.getPageSize() > 0 ? page.getPageSize() : 10;
        int pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        page.setTotal(total);
        page.setPages(pages);
        page.setIsFirstPage(pageNum <= 1);
        page.setIsLastPage(pages == 0 || pageNum >= pages);
        page.setHasPreviousPage(pageNum > 1);
        page.setHasNextPage(pageNum < pages);
        page.setPrePage(pageNum > 1 ? pageNum - 1 : 0);
        page.setNextPage(pageNum < pages ? pageNum + 1 : 0);
    }

    private String displayName(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return "用户#" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return "用户#" + userId;
    }

    static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
