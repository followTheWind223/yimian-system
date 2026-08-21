package com.yimian.system.service;

import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.TopicVO;

import java.util.List;

/**
 * 热点数据服务
 */
public interface HotDataService {

    /** 记录一次题目浏览 */
    void incrView(Long knowledgeId);

    /** 记录一次点赞 */
    void incrLike(Long knowledgeId);

    /** 记录一次收藏 */
    void incrCollect(Long knowledgeId);

    /** 记录一次评论 */
    void incrComment(Long knowledgeId);

    /** 今日热门题目 */
    List<KnowledgeVO> getTodayHotKnowledge(Integer limit);

    /** 本周热门题目 */
    List<KnowledgeVO> getWeeklyHotKnowledge(Integer limit);

    void incrTopic(Long topicId, int delta);

    List<TopicVO> getHotTopics(Integer limit);

    /** 重建 Redis 热榜 */
    void rebuildKnowledgeHotRankings();
}
