package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.CommentCreateDto;
import com.yimian.system.dto.CommentQueryDto;
import com.yimian.system.vo.CommentLikeVO;
import com.yimian.system.vo.CommentVO;

public interface CommentService {

    PageInfo<CommentVO> list(CommentQueryDto query, Long currentUserId);

    CommentVO create(CommentCreateDto dto, Long userId);

    CommentLikeVO toggleLike(Long id, Long userId);
}
