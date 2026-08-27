package com.yimian.system.vo;

public record AgentProviderVO(
        Long id,
        String providerCode,
        String displayName,
        String protocolType,
        String baseUrl,
        Boolean credentialConfigured,
        String credentialMasked,
        Integer status,
        Integer configVersion,
        String lastTestStatus,
        String lastTestMessage,
        String lastTestAt
) {
}
