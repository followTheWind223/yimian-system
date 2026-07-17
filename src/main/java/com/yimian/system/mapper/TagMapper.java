package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签 Mapper
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据ID列表批量查询标签
     */
    List<Tag> selectByIds(@Param("ids") List<Long> ids);
}
