package com.yimian.system.service;

import com.yimian.system.vo.AgentConversationDetailVO;
import com.yimian.system.vo.AgentConversationSessionPageVO;
import com.yimian.system.vo.AgentConversationStatsVO;

public interface AgentConversationAuditService {

    AgentConversationStatsVO getStats();

    AgentConversationSessionPageVO listSessions(
            int page,
            int size,
            Long userId,
            String sessionType,
            Integer status
    );

    AgentConversationDetailVO getSessionDetail(Long sessionId, int page, int size);
}
