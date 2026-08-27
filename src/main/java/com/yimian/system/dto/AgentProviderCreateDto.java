package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Agent model provider configuration")
public class AgentProviderCreateDto {
    @NotBlank @Size(max = 48) @Pattern(regexp = "^[a-z][a-z0-9_-]*$")
    private String providerCode;
    @NotBlank @Size(max = 120)
    private String displayName;
    @NotBlank @Pattern(regexp = "^(openai_compatible|anthropic)$")
    private String protocolType = "openai_compatible";
    @NotBlank @Size(max = 512)
    private String baseUrl;
    @Size(max = 512)
    @Schema(description = "Required for a new provider")
    private String apiKey;
    private Integer status = 1;
}
