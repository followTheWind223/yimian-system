package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.KnowledgeTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识-标签关联 Mapper
 */
public interface KnowledgeTagMapper extends BaseMapper<KnowledgeTag> {

    /**
     * 批量插入关联
     */
    int insertBatch(@Param("knowledgeId") Long knowledgeId, @Param("tagIds") List<Long> tagIds);

    /**
     * 删除某知识的所有标签关联
     */
    int deleteByKnowledgeId(@Param("knowledgeId") Long knowledgeId);

    /**
     * 删除某标签的所有关联
     */
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 查询某知识关联的标签ID列表
     */
    List<Long> selectTagIdsByKnowledgeId(@Param("knowledgeId") Long knowledgeId);
}
