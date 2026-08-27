package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Agent model deployment update")
public class AgentModelUpdateDto {
    @Size(max = 80) @Pattern(regexp = "^[a-z][a-z0-9_.-]*$") private String modelCode;
    @Min(1) private Long providerAccountId;
    @Size(max = 160) private String upstreamModelName;
    @Size(max = 120) private String displayName;
    @Pattern(regexp = "^(chat|embedding|rerank)$") private String modelType;
    @Min(1024) private Integer contextWindow;
    @Min(1) private Integer maxOutputTokens;
    private Map<String, Object> capabilities;
    private Map<String, Object> defaultParams;
    @DecimalMin("0") private BigDecimal inputPrice;
    @DecimalMin("0") private BigDecimal outputPrice;
    @DecimalMin("0") private BigDecimal cachedInputPrice;
    @DecimalMin("0") private BigDecimal reasoningPrice;
    private Integer status;
}
