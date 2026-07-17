package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.BlogImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogImageMapper extends BaseMapper<BlogImage> {

    List<BlogImage> selectByBlogId(@Param("blogId") Long blogId);

    int deleteByBlogId(@Param("blogId") Long blogId);
}
