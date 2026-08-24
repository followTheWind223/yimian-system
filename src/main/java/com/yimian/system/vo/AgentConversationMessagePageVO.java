package com.yimian.system.vo;

import java.util.List;

public record AgentConversationMessagePageVO(
        List<AgentConversationMessageVO> list,
        long total,
        int pageNum,
        int pageSize
) {
}
