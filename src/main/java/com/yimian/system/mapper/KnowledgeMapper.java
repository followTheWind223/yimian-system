package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Knowledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识题目 Mapper
 */
public interface KnowledgeMapper extends BaseMapper<Knowledge> {

    /**
     * 根据内容哈希查询
     */
    Knowledge selectByContentHash(@Param("hash") String hash);

    /**
     * 分页查询
     */
    List<Knowledge> selectPage(@Param("keyword") String keyword,
                               @Param("difficulty") Integer difficulty,
                               @Param("tagId") Long tagId,
                               @Param("status") Integer status);

    /**
     * 查询知识的标签ID列表
     */
    List<Long> selectTagIdsByKnowledgeId(@Param("knowledgeId") Long knowledgeId);

    /**
     * 增加浏览次数
     */
    int incrementViewCount(@Param("id") Long id);

    int updateCollectCount(@Param("id") Long id, @Param("delta") Integer delta);

    int updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);

    int updateCommentCount(@Param("id") Long id, @Param("delta") Integer delta);

    /**
     * 按 ID 批量查询已审核通过的题目
     */
    List<Knowledge> selectApprovedByIds(@Param("ids") List<Long> ids);

    /**
     * MySQL 兜底热度排序
     */
    List<Knowledge> selectHotKnowledge(@Param("limit") Integer limit);

    /**
     * 我的题目列表（分页）
     */
    List<Knowledge> selectMyPage(@Param("userId") Long userId,
                                  @Param("status") Integer status);
}
