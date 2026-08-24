package com.yimian.system.vo;

import java.util.List;

public record AgentConversationSessionPageVO(
        List<AgentConversationSessionVO> list,
        long total,
        int pageNum,
        int pageSize
) {
}
