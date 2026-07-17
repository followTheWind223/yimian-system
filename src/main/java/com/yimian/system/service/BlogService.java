package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.BlogCreateDto;
import com.yimian.system.dto.BlogQueryDto;
import com.yimian.system.dto.BlogUpdateDto;
import com.yimian.system.vo.BlogVO;

public interface BlogService {

    BlogVO create(BlogCreateDto dto, Long userId);

    BlogVO update(Long id, BlogUpdateDto dto, Long userId);

    void delete(Long id, Long userId);

    PageInfo<BlogVO> list(BlogQueryDto query);

    PageInfo<BlogVO> adminList(Integer page, Integer size, String keyword, Integer status);

    PageInfo<BlogVO> myList(Integer page, Integer size, Integer status, Long userId);

    PageInfo<BlogVO> publicListByUser(Integer page, Integer size, Long userId);

    BlogVO getDetail(Long id, Long currentUserId);

    int toggleLike(Long id, Long userId);

    int collect(Long id, Long folderId, Long userId);

    BlogVO adminUpdateStatus(Long id, Integer status);

    void adminDelete(Long id);
}
