package com.yimian.system.vo;

public record AgentProfileModelVO(
        String profileCode,
        String modelCode,
        String modelDisplayName,
        String providerCode,
        Boolean isDefault,
        Boolean userSelectable,
        Integer fallbackPriority,
        Integer status
) {
}
