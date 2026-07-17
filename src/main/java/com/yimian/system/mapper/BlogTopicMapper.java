package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.BlogTopic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogTopicMapper extends BaseMapper<BlogTopic> {

    List<Long> selectTopicIdsByBlogId(@Param("blogId") Long blogId);

    int deleteByBlogId(@Param("blogId") Long blogId);

    int insertBatch(@Param("blogId") Long blogId, @Param("topicIds") List<Long> topicIds);
}
