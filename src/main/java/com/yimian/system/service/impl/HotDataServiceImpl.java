package com.yimian.system.service.impl;

import com.yimian.system.entity.Knowledge;
import com.yimian.system.entity.Tag;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.KnowledgeMapper;
import com.yimian.system.mapper.KnowledgeTagMapper;
import com.yimian.system.mapper.TagMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.HotDataService;
import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.KnowledgeVO.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotDataServiceImpl implements HotDataService {

    private static final String DAILY_RANK_KEY = "hot:knowledge:daily";
    private static final String WEEKLY_RANK_KEY = "hot:knowledge:weekly";
    private static final String COUNTER_KEY_FORMAT = "hot:knowledge:%s:%s";
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    private static final int COUNTER_KEEP_DAYS = 14;

    private static final String VIEW = "view";
    private static final String LIKE = "like";
    private static final String COLLECT = "collect";
    private static final String COMMENT = "comment";

    private final StringRedisTemplate stringRedisTemplate;
    private final KnowledgeMapper knowledgeMapper;
    private final KnowledgeTagMapper knowledgeTagMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    @Override
    public void incrView(Long knowledgeId) {
        recordEvent(knowledgeId, VIEW, 1);
    }

    @Override
    public void incrLike(Long knowledgeId) {
        recordEvent(knowledgeId, LIKE, 3);
    }

    @Override
    public void incrCollect(Long knowledgeId) {
        recordEvent(knowledgeId, COLLECT, 5);
    }

    @Override
    public void incrComment(Long knowledgeId) {
        recordEvent(knowledgeId, COMMENT, 2);
    }

    @Override
    public List<KnowledgeVO> getTodayHotKnowledge(Integer limit) {
        return getHotKnowledge(DAILY_RANK_KEY, normalizeLimit(limit));
    }

    @Override
    public List<KnowledgeVO> getWeeklyHotKnowledge(Integer limit) {
        return getHotKnowledge(WEEKLY_RANK_KEY, normalizeLimit(limit));
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void rebuildKnowledgeHotRankings() {
        try {
            LocalDate today = LocalDate.now();
            rebuildRank(DAILY_RANK_KEY, List.of(today));

            List<LocalDate> weekDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                weekDays.add(today.minusDays(i));
            }
            rebuildRank(WEEKLY_RANK_KEY, weekDays);
            log.info("Knowledge hot rankings rebuilt");
        } catch (Exception e) {
            log.warn("Failed to rebuild knowledge hot rankings", e);
        }
    }

    private void recordEvent(Long knowledgeId, String metric, double weight) {
        if (knowledgeId == null) {
            return;
        }
        try {
            String member = knowledgeId.toString();
            String counterKey = counterKey(metric, LocalDate.now());
            stringRedisTemplate.opsForHash().increment(counterKey, member, 1);
            stringRedisTemplate.expire(counterKey, Duration.ofDays(COUNTER_KEEP_DAYS));
            stringRedisTemplate.opsForZSet().incrementScore(DAILY_RANK_KEY, member, weight);
            stringRedisTemplate.opsForZSet().incrementScore(WEEKLY_RANK_KEY, member, weight);
        } catch (Exception e) {
            log.warn("Failed to record knowledge hot event: knowledgeId={}, metric={}", knowledgeId, metric, e);
        }
    }

    private List<KnowledgeVO> getHotKnowledge(String rankKey, int limit) {
        try {
            Set<String> members = stringRedisTemplate.opsForZSet().reverseRange(rankKey, 0, limit - 1);
            if (members != null && !members.isEmpty()) {
                List<Long> ids = members.stream()
                        .map(this::parseLong)
                        .filter(id -> id != null)
                        .collect(Collectors.toList());
                List<KnowledgeVO> hotList = toKnowledgeVOList(ids);
                if (!hotList.isEmpty()) {
                    return hotList;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to query Redis knowledge hot ranking: key={}", rankKey, e);
        }
        return knowledgeMapper.selectHotKnowledge(limit).stream()
                .map(this::toKnowledgeVO)
                .collect(Collectors.toList());
    }

    private void rebuildRank(String rankKey, List<LocalDate> days) {
        Set<Long> knowledgeIds = collectKnowledgeIds(days);
        if (knowledgeIds.isEmpty()) {
            stringRedisTemplate.delete(rankKey);
            return;
        }

        List<Knowledge> knowledgeList = knowledgeMapper.selectApprovedByIds(new ArrayList<>(knowledgeIds));
        Map<Long, Knowledge> knowledgeMap = knowledgeList.stream()
                .collect(Collectors.toMap(Knowledge::getId, item -> item));

        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (Long knowledgeId : knowledgeIds) {
            Knowledge knowledge = knowledgeMap.get(knowledgeId);
            if (knowledge == null) {
                continue;
            }
            double score = calculateHotScore(knowledge, days);
            if (score > 0) {
                tuples.add(new DefaultTypedTuple<>(knowledgeId.toString(), score));
            }
        }

        stringRedisTemplate.delete(rankKey);
        if (!tuples.isEmpty()) {
            stringRedisTemplate.opsForZSet().add(rankKey, tuples);
        }
    }

    private Set<Long> collectKnowledgeIds(List<LocalDate> days) {
        Set<Long> ids = new HashSet<>();
        for (LocalDate day : days) {
            for (String metric : List.of(VIEW, LIKE, COLLECT, COMMENT)) {
                Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(counterKey(metric, day));
                for (Object key : entries.keySet()) {
                    Long id = parseLong(String.valueOf(key));
                    if (id != null) {
                        ids.add(id);
                    }
                }
            }
        }
        return ids;
    }

    private double calculateHotScore(Knowledge knowledge, List<LocalDate> days) {
        double eventScore = 0;
        for (LocalDate day : days) {
            eventScore += counterValue(VIEW, day, knowledge.getId()) * 1;
            eventScore += counterValue(LIKE, day, knowledge.getId()) * 3;
            eventScore += counterValue(COLLECT, day, knowledge.getId()) * 5;
            eventScore += counterValue(COMMENT, day, knowledge.getId()) * 2;
        }

        long daysSincePublish = 0;
        if (knowledge.getCreatedAt() != null) {
            daysSincePublish = Math.max(0, ChronoUnit.DAYS.between(knowledge.getCreatedAt().toLocalDate(), LocalDate.now()));
        }
        double timeDecay = 1D / (1D + daysSincePublish * 0.1D);
        return eventScore * timeDecay;
    }

    private double counterValue(String metric, LocalDate day, Long knowledgeId) {
        Object value = stringRedisTemplate.opsForHash().get(counterKey(metric, day), knowledgeId.toString());
        if (value == null) {
            return 0D;
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0D;
        }
    }

    private List<KnowledgeVO> toKnowledgeVOList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Knowledge> knowledgeList = knowledgeMapper.selectApprovedByIds(ids);
        Map<Long, Knowledge> knowledgeMap = knowledgeList.stream()
                .collect(Collectors.toMap(Knowledge::getId, item -> item, (a, b) -> a, LinkedHashMap::new));
        List<KnowledgeVO> result = new ArrayList<>();
        for (Long id : ids) {
            Knowledge knowledge = knowledgeMap.get(id);
            if (knowledge != null) {
                result.add(toKnowledgeVO(knowledge));
            }
        }
        return result;
    }

    private KnowledgeVO toKnowledgeVO(Knowledge entity) {
        KnowledgeVO vo = new KnowledgeVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setDifficulty(entity.getDifficulty());
        vo.setStatus(entity.getStatus());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setAuditUserId(entity.getAuditUserId());
        vo.setAuditTime(entity.getAuditTime());
        vo.setSubmitUserId(entity.getSubmitUserId());
        User submitUser = findUser(entity.getSubmitUserId());
        User auditUser = findUser(entity.getAuditUserId());
        vo.setSubmitUserName(displayNameOf(submitUser, entity.getSubmitUserId()));
        vo.setSubmitUserAvatar(avatarOf(submitUser));
        vo.setAuditUserName(displayNameOf(auditUser, entity.getAuditUserId()));
        vo.setAuditUserAvatar(avatarOf(auditUser));
        vo.setViewCount(entity.getViewCount() != null ? entity.getViewCount() : 0);
        vo.setLikeCount(entity.getLikeCount() != null ? entity.getLikeCount() : 0);
        vo.setCollectCount(entity.getCollectCount() != null ? entity.getCollectCount() : 0);
        vo.setCommentCount(entity.getCommentCount() != null ? entity.getCommentCount() : 0);
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        List<Long> tagIds = knowledgeTagMapper.selectTagIdsByKnowledgeId(entity.getId());
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectByIds(tagIds);
            vo.setTags(tags.stream().map(tag -> {
                TagVO tagVO = new TagVO();
                tagVO.setId(tag.getId());
                tagVO.setName(tag.getName());
                tagVO.setColor(tag.getColor());
                return tagVO;
            }).collect(Collectors.toList()));
        } else {
            vo.setTags(Collections.emptyList());
        }
        return vo;
    }

    private String counterKey(String metric, LocalDate day) {
        return String.format(COUNTER_KEY_FORMAT, metric, day.toString().replace("-", ""));
    }

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    private String displayNameOf(User user, Long userId) {
        if (userId == null) {
            return null;
        }
        if (user == null) {
            return "用户#" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return "用户#" + userId;
    }

    private String avatarOf(User user) {
        return user == null ? null : user.getAvatar();
    }

    private String displayName(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return "用户#" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return "用户#" + userId;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
