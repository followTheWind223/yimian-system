package com.yimian.system.vo;

public record AgentProfileVO(
        Long id,
        String profileCode,
        String displayName,
        String sessionType,
        String promptVersion,
        Boolean allowUserModelSelection,
        Integer status,
        Integer configVersion
) {
}
