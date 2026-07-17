package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper extends BaseMapper<Blog> {

    List<Blog> selectPublishedPage(@Param("keyword") String keyword,
                                   @Param("refType") String refType,
                                   @Param("sort") String sort);

    List<Blog> selectMyPage(@Param("authorId") Long authorId,
                            @Param("status") Integer status);

    List<Blog> selectAdminPage(@Param("keyword") String keyword,
                               @Param("status") Integer status);

    int incrementViewCount(@Param("id") Long id);

    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    int updateCommentCount(@Param("id") Long id, @Param("delta") int delta);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
