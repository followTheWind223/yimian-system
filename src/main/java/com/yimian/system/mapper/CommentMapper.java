package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> selectRootComments(@Param("targetType") String targetType,
                                     @Param("targetId") Long targetId,
                                     @Param("sort") String sort);

    List<Comment> selectRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

    int updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);

    int updateReplyCount(@Param("id") Long id, @Param("delta") Integer delta);

    int countByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
}
