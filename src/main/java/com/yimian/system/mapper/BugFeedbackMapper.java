package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.BugFeedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BugFeedbackMapper extends BaseMapper<BugFeedback> {

    List<BugFeedback> selectFeedbackPage(@Param("keyword") String keyword,
                                         @Param("status") Integer status);
}
