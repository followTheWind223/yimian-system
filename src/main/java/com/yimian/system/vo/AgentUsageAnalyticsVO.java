package com.yimian.system.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AgentUsageAnalyticsVO(
        int days,
        String granularity,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Summary summary,
        List<TokenTrendPoint> tokenTrend,
        List<ModelUsage> modelUsage,
        List<SessionTypeUsage> sessionTypeUsage
) {
    public record Summary(
            long sessionCount,
            long activeUserCount,
            long messageCount,
            long inputTokens,
            long outputTokens,
            long reasoningTokens,
            long cachedInputTokens,
            long totalTokens,
            long estimatedMessageCount,
            BigDecimal totalCostUsd,
            BigDecimal averageLatencyMs
    ) {
    }

    public record TokenTrendPoint(
            LocalDateTime bucketStart,
            long sessionCount,
            long messageCount,
            long inputTokens,
            long outputTokens,
            long reasoningTokens,
            long cachedInputTokens,
            long totalTokens,
            BigDecimal totalCostUsd
    ) {
    }

    public record ModelUsage(
            String modelProvider,
            String modelName,
            long messageCount,
            long totalTokens,
            BigDecimal totalCostUsd,
            BigDecimal averageLatencyMs
    ) {
    }

    public record SessionTypeUsage(
            String sessionType,
            long sessionCount,
            long messageCount,
            long totalTokens,
            BigDecimal totalCostUsd
    ) {
    }
}
