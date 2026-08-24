package com.yimian.system.vo;

public record AgentConversationDetailVO(
        AgentConversationSessionVO session,
        AgentConversationMessagePageVO messages
) {
}
