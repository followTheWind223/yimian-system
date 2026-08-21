package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.BugFeedbackCreateDto;
import com.yimian.system.dto.BugFeedbackQueryDto;
import com.yimian.system.vo.BugFeedbackVO;

public interface BugFeedbackService {

    BugFeedbackVO create(BugFeedbackCreateDto dto, Long userId);

    PageInfo<BugFeedbackVO> list(BugFeedbackQueryDto query);

    BugFeedbackVO detail(Long id);

    BugFeedbackVO updateStatus(Long id, Integer status);
}
