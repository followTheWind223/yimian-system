package com.yimian.system.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatVO {
    private String reply;
    @Builder.Default
    private List<AgentToolCallVO> toolCalls = new ArrayList<>();
    private String sessionId;
    private String requestId;
}
