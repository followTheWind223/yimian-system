package com.yimian.system.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AgentConversationMessageVO(
        String id,
        String sequenceNo,
        String role,
        String messageType,
        int status,
        String content,
        String toolName,
        String modelProvider,
        String modelName,
        long contentTokens,
        long inputTokens,
        long outputTokens,
        long reasoningTokens,
        long cachedInputTokens,
        long totalTokens,
        String tokenizerModel,
        boolean tokenEstimated,
        Long latencyMs,
        BigDecimal costUsd,
        String finishReason,
        String errorCode,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {
}
