package com.yimian.system.vo;

import java.math.BigDecimal;
import java.util.Map;

public record AgentModelVO(
        Long id,
        String modelCode,
        Long providerAccountId,
        String providerCode,
        String providerDisplayName,
        String upstreamModelName,
        String displayName,
        String modelType,
        Integer contextWindow,
        Integer maxOutputTokens,
        Map<String, Object> capabilities,
        Map<String, Object> defaultParams,
        BigDecimal inputPrice,
        BigDecimal outputPrice,
        BigDecimal cachedInputPrice,
        BigDecimal reasoningPrice,
        Integer status,
        Integer configVersion
) {
}
