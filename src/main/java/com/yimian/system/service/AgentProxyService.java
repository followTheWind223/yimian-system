package com.yimian.system.service;

import com.yimian.system.dto.AgentChatDto;
import com.yimian.system.vo.AgentChatVO;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AgentProxyService {
    AgentChatVO chat(Long userId, AgentChatDto dto);
    StreamingResponseBody streamChat(Long userId, AgentChatDto dto);
    void clearSession(Long userId, String sessionId);
}
