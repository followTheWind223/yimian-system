package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.BlogLike;
import org.apache.ibatis.annotations.Param;

public interface BlogLikeMapper extends BaseMapper<BlogLike> {

    BlogLike selectActive(@Param("blogId") Long blogId, @Param("userId") Long userId);

    BlogLike selectAny(@Param("blogId") Long blogId, @Param("userId") Long userId);

    int softDelete(@Param("blogId") Long blogId, @Param("userId") Long userId);

    int restore(@Param("id") Long id);
}
