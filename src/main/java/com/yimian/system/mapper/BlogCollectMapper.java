package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.BlogCollect;
import org.apache.ibatis.annotations.Param;

public interface BlogCollectMapper extends BaseMapper<BlogCollect> {

    BlogCollect selectActive(@Param("blogId") Long blogId, @Param("folderId") Long folderId, @Param("userId") Long userId);
}
