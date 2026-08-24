package com.yimian.system.vo;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentToolCallVO {
    private String name;
    @Builder.Default
    private Map<String, Object> args = new LinkedHashMap<>();
    private String id;
}
