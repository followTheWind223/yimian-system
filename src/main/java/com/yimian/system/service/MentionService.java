package com.yimian.system.service;

public interface MentionService {

    void notifyMentions(Long senderId,
                        String content,
                        String targetType,
                        Long targetId,
                        String sourceType,
                        Long sourceId);
}
