package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Agent model provider update")
public class AgentProviderUpdateDto {
    @Size(max = 120)
    private String displayName;
    @Pattern(regexp = "^(openai_compatible|anthropic)$")
    private String protocolType;
    @Size(max = 512)
    private String baseUrl;
    @Size(max = 512)
    @Schema(description = "Blank keeps the current key")
    private String apiKey;
    private Integer status;
}
