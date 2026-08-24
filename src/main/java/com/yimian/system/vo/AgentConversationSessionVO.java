package com.yimian.system.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AgentConversationSessionVO(
        String id,
        String userId,
        String sessionType,
        String scene,
        String title,
        int status,
        boolean deleted,
        long messageCount,
        long inputTokens,
        long outputTokens,
        long reasoningTokens,
        long cachedInputTokens,
        long totalTokens,
        BigDecimal totalCostUsd,
        String modelProvider,
        String modelName,
        LocalDateTime lastActiveAt,
        LocalDateTime createdAt,
        LocalDateTime archivedAt,
        LocalDateTime deletedAt
) {
}
