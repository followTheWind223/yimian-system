package com.yimian.system.service;

import com.yimian.system.entity.Tag;
import com.yimian.system.vo.TagVO;

import java.util.List;

/**
 * 标签服务
 */
public interface TagService {

    /**
     * 标签列表（公开），按关联知识数降序
     */
    List<TagVO> list(String keyword);

    /**
     * 标签详情
     */
    TagVO getById(Long id);

    /**
     * 新增标签（管理员）
     */
    TagVO create(Tag tag);

    /**
     * 编辑标签（管理员）
     */
    TagVO update(Long id, Tag tag);

    /**
     * 删除标签（管理员）
     */
    void delete(Long id);
}
