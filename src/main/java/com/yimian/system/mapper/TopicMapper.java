package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper extends BaseMapper<Topic> {

    List<Topic> selectAll(@Param("keyword") String keyword);

    List<Topic> selectByIds(@Param("ids") List<Long> ids);

    int incrementBlogCount(@Param("id") Long id, @Param("delta") int delta);
}
