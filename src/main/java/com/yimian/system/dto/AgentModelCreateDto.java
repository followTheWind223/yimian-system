package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Agent model deployment configuration")
public class AgentModelCreateDto {
    @NotBlank @Size(max = 80) @Pattern(regexp = "^[a-z][a-z0-9_.-]*$")
    private String modelCode;
    @NotNull @Min(1)
    private Long providerAccountId;
    @NotBlank @Size(max = 160)
    private String upstreamModelName;
    @NotBlank @Size(max = 120)
    private String displayName;
    @NotBlank @Pattern(regexp = "^(chat|embedding|rerank)$")
    private String modelType = "chat";
    @Min(1024) private Integer contextWindow = 32768;
    @Min(1) private Integer maxOutputTokens = 4096;
    private Map<String, Object> capabilities = Map.of();
    private Map<String, Object> defaultParams = Map.of();
    @DecimalMin("0") private BigDecimal inputPrice = BigDecimal.ZERO;
    @DecimalMin("0") private BigDecimal outputPrice = BigDecimal.ZERO;
    @DecimalMin("0") private BigDecimal cachedInputPrice = BigDecimal.ZERO;
    @DecimalMin("0") private BigDecimal reasoningPrice = BigDecimal.ZERO;
    private Integer status = 1;
}
