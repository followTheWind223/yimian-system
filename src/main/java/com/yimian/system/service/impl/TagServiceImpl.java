package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.entity.Knowledge;
import com.yimian.system.entity.Tag;
import com.yimian.system.mapper.KnowledgeMapper;
import com.yimian.system.mapper.KnowledgeTagMapper;
import com.yimian.system.mapper.TagMapper;
import com.yimian.system.service.TagService;
import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final KnowledgeTagMapper knowledgeTagMapper;
    private final KnowledgeMapper knowledgeMapper;

    @Override
    public List<TagVO> list(String keyword) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Tag::getSort);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Tag::getName, keyword);
        }
        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public TagVO getById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        return toVO(tag);
    }

    @Override
    @Transactional
    public TagVO create(Tag tag) {
        // 名称唯一校验
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.TAG_NAME_EXISTS);
        }
        tagMapper.insert(tag);
        log.info("新增标签成功: id={}, name={}", tag.getId(), tag.getName());
        return toVO(tag);
    }

    @Override
    @Transactional
    public TagVO update(Long id, Tag tag) {
        Tag existing = tagMapper.selectById(id);
        if (existing == null) throw new BusinessException(ResultCode.TAG_NOT_FOUND);

        // 名称唯一校验
        if (tag.getName() != null) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, tag.getName()).ne(Tag::getId, id);
            if (tagMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.TAG_NAME_EXISTS);
            }
        }

        if (tag.getName() != null) existing.setName(tag.getName());
        if (tag.getColor() != null) existing.setColor(tag.getColor());
        if (tag.getSort() != null) existing.setSort(tag.getSort());
        tagMapper.updateById(existing);

        log.info("编辑标签成功: id={}, name={}", id, existing.getName());
        return toVO(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag existing = tagMapper.selectById(id);
        if (existing == null) throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        // 删除关联表
        knowledgeTagMapper.deleteByTagId(id);
        tagMapper.deleteById(id);
        log.info("删除标签成功: id={}, name={}", id, existing.getName());
    }

    // ==================== 私有方法 ====================

    private TagVO toVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setColor(tag.getColor());
        vo.setSort(tag.getSort());
        vo.setCreatedAt(tag.getCreatedAt());
        vo.setUpdatedAt(tag.getUpdatedAt());

        // 统计关联知识数量
        Long count = knowledgeTagMapper.selectCount(
            new LambdaQueryWrapper<com.yimian.system.entity.KnowledgeTag>()
                .eq(com.yimian.system.entity.KnowledgeTag::getTagId, tag.getId())
        );
        vo.setUsageCount(count != null ? count.intValue() : 0);

        return vo;
    }
}
