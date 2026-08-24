package com.yimian.system.vo;

import java.math.BigDecimal;

public record AgentConversationStatsVO(
        long sessionCount,
        long activeSessionCount,
        long supportSessionCount,
        long quickSessionCount,
        long messageCount,
        long inputTokens,
        long outputTokens,
        long reasoningTokens,
        long cachedInputTokens,
        long totalTokens,
        long estimatedMessageCount,
        BigDecimal totalCostUsd
) {
}
