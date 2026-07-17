package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.entity.Topic;
import com.yimian.system.mapper.TopicMapper;
import com.yimian.system.service.TopicService;
import com.yimian.system.vo.TopicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;

    @Override
    public List<TopicVO> list(String keyword) {
        List<Topic> topics = topicMapper.selectAll(keyword);
        return topics.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public TopicVO create(String name, String description, String color) {
        String trimmedName = trimToNull(name);
        if (trimmedName == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话题名称不能为空");
        }
        if (trimmedName.length() > 50) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话题名称不能超过50字");
        }
        // 检查重名
        Long count = topicMapper.selectCount(new LambdaQueryWrapper<Topic>()
                .eq(Topic::getName, trimmedName));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "话题名称已存在");
        }
        Topic topic = new Topic();
        topic.setName(trimmedName);
        topic.setDescription(trimToNull(description));
        topic.setColor(trimToNull(color));
        topic.setSort(0);
        topic.setBlogCount(0);
        topic.setDeleted(0);
        topicMapper.insert(topic);
        log.info("Topic created: id={}, name={}", topic.getId(), trimmedName);
        return toVO(topic);
    }

    @Override
    public TopicVO update(Long id, String name, String description, String color, Integer sort) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null || Integer.valueOf(1).equals(topic.getDeleted())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (name != null && !name.isBlank()) {
            String trimmed = name.trim();
            if (trimmed.length() > 50) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "话题名称不能超过50字");
            }
            // 检查重名
            Long count = topicMapper.selectCount(new LambdaQueryWrapper<Topic>()
                    .eq(Topic::getName, trimmed)
                    .ne(Topic::getId, id));
            if (count > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "话题名称已存在");
            }
            topic.setName(trimmed);
        }
        if (description != null) {
            topic.setDescription(trimToNull(description));
        }
        if (color != null) {
            topic.setColor(trimToNull(color));
        }
        if (sort != null) {
            topic.setSort(sort);
        }
        topicMapper.updateById(topic);
        log.info("Topic updated: id={}", id);
        return toVO(topic);
    }

    @Override
    public void delete(Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null || Integer.valueOf(1).equals(topic.getDeleted())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        topicMapper.deleteById(id);
        log.info("Topic deleted: id={}", id);
    }

    private TopicVO toVO(Topic topic) {
        TopicVO vo = new TopicVO();
        vo.setId(topic.getId());
        vo.setName(topic.getName());
        vo.setDescription(topic.getDescription());
        vo.setColor(topic.getColor());
        vo.setSort(topic.getSort());
        vo.setBlogCount(topic.getBlogCount() != null ? topic.getBlogCount() : 0);
        vo.setCreatedAt(topic.getCreatedAt());
        vo.setUpdatedAt(topic.getUpdatedAt());
        return vo;
    }

    private String trimToNull(String text) {
        if (text == null) return null;
        String value = text.trim();
        return value.isEmpty() ? null : value;
    }
}
