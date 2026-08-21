package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.entity.Topic;
import com.yimian.system.mapper.TopicMapper;
import com.yimian.system.service.HotDataService;
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
    private final HotDataService hotDataService;

    @Override
    public List<TopicVO> list(String keyword) {
        String searchKeyword = trimToNull(keyword);
        if (searchKeyword == null) {
            List<TopicVO> hotTopics = hotDataService.getHotTopics(50);
            if (!hotTopics.isEmpty()) {
                List<Long> hotIds = hotTopics.stream()
                        .map(TopicVO::getId)
                        .collect(Collectors.toList());
                List<TopicVO> rest = topicMapper.selectAll(null).stream()
                        .filter(topic -> !hotIds.contains(topic.getId()))
                        .map(this::toVO)
                        .collect(Collectors.toList());
                hotTopics.addAll(rest);
                return hotTopics;
            }
        }
        List<Topic> topics = topicMapper.selectAll(searchKeyword);
        return topics.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public TopicVO findOrCreate(String name, String description, String color) {
        String trimmedName = validateTopicName(name);
        Topic existing = topicMapper.selectOne(new LambdaQueryWrapper<Topic>()
                .eq(Topic::getName, trimmedName));
        if (existing != null && !Integer.valueOf(1).equals(existing.getDeleted())) {
            return toVO(existing);
        }
        Topic topic = new Topic();
        topic.setName(trimmedName);
        topic.setDescription(trimToNull(description));
        topic.setColor(trimToNull(color));
        topic.setSort(0);
        topic.setBlogCount(0);
        topic.setDeleted(0);
        topicMapper.insert(topic);
        log.info("Topic created by user: id={}, name={}", topic.getId(), trimmedName);
        return toVO(topic);
    }

    @Override
    public TopicVO create(String name, String description, String color) {
        String trimmedName = validateTopicName(name);
        Long count = topicMapper.selectCount(new LambdaQueryWrapper<Topic>()
                .eq(Topic::getName, trimmedName));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "topic name already exists");
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
            String trimmed = validateTopicName(name);
            Long count = topicMapper.selectCount(new LambdaQueryWrapper<Topic>()
                    .eq(Topic::getName, trimmed)
                    .ne(Topic::getId, id));
            if (count > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "topic name already exists");
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
        if (text == null) {
            return null;
        }
        String value = text.trim();
        return value.isEmpty() ? null : value;
    }

    private String validateTopicName(String name) {
        String trimmedName = trimToNull(name);
        if (trimmedName == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "topic name is required");
        }
        if (trimmedName.length() > 50) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "topic name must be at most 50 characters");
        }
        return trimmedName;
    }
}
