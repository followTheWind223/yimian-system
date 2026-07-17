package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.CommentLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    CommentLike selectActive(@Param("commentId") Long commentId, @Param("userId") Long userId);

    CommentLike selectAny(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int softDelete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int restore(@Param("id") Long id);

    List<Long> selectLikedCommentIds(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);
}
