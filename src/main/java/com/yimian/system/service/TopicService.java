package com.yimian.system.service;

import com.yimian.system.vo.TopicVO;

import java.util.List;

public interface TopicService {

    List<TopicVO> list(String keyword);

    TopicVO create(String name, String description, String color);

    TopicVO update(Long id, String name, String description, String color, Integer sort);

    void delete(Long id);
}
