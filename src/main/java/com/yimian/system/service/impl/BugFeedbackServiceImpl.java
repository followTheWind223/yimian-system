package com.yimian.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.BugFeedbackCreateDto;
import com.yimian.system.dto.BugFeedbackQueryDto;
import com.yimian.system.entity.BugFeedback;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.BugFeedbackMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.BugFeedbackService;
import com.yimian.system.vo.BugFeedbackVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BugFeedbackServiceImpl implements BugFeedbackService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_IGNORED = 3;

    private final BugFeedbackMapper bugFeedbackMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public BugFeedbackVO create(BugFeedbackCreateDto dto, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        BugFeedback feedback = new BugFeedback();
        feedback.setUserId(userId);
        feedback.setUsername(user.getUsername());
        feedback.setTitle(trim(dto.getTitle()));
        feedback.setContent(trim(dto.getContent()));
        feedback.setPageUrl(trim(dto.getPageUrl()));
        feedback.setContact(trim(dto.getContact()));
        feedback.setStatus(STATUS_PENDING);
        feedback.setDeleted(0);
        bugFeedbackMapper.insert(feedback);
        return toVO(feedback);
    }

    @Override
    public PageInfo<BugFeedbackVO> list(BugFeedbackQueryDto query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 50);

        PageHelper.startPage(page, size);
        List<BugFeedback> records = bugFeedbackMapper.selectFeedbackPage(trim(query.getKeyword()), query.getStatus());
        List<BugFeedbackVO> list = records.stream().map(this::toVO).collect(Collectors.toList());
        return new PageInfo<>(list);
    }

    @Override
    public BugFeedbackVO detail(Long id) {
        BugFeedback feedback = bugFeedbackMapper.selectById(id);
        if (feedback == null || Integer.valueOf(1).equals(feedback.getDeleted())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "反馈不存在");
        }
        return toVO(feedback);
    }

    @Override
    @Transactional
    public BugFeedbackVO updateStatus(Long id, Integer status) {
        if (status == null || status < STATUS_PENDING || status > STATUS_IGNORED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "反馈状态不正确");
        }
        BugFeedback feedback = bugFeedbackMapper.selectById(id);
        if (feedback == null || Integer.valueOf(1).equals(feedback.getDeleted())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "反馈不存在");
        }
        feedback.setStatus(status);
        bugFeedbackMapper.updateById(feedback);
        return toVO(feedback);
    }

    private BugFeedbackVO toVO(BugFeedback feedback) {
        BugFeedbackVO vo = new BugFeedbackVO();
        BeanUtils.copyProperties(feedback, vo);
        return vo;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
