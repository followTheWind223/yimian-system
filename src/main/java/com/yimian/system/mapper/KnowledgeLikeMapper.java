package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.KnowledgeLike;
import org.apache.ibatis.annotations.Param;

public interface KnowledgeLikeMapper extends BaseMapper<KnowledgeLike> {

    KnowledgeLike selectActive(@Param("knowledgeId") Long knowledgeId, @Param("userId") Long userId);

    KnowledgeLike selectAny(@Param("knowledgeId") Long knowledgeId, @Param("userId") Long userId);

    int softDelete(@Param("knowledgeId") Long knowledgeId, @Param("userId") Long userId);

    int restore(@Param("id") Long id);
}
