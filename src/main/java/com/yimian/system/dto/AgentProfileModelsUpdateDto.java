package com.yimian.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Agent profile model bindings")
public class AgentProfileModelsUpdateDto {
    @Valid
    @Size(max = 50)
    private List<Binding> bindings = List.of();

    @Data
    public static class Binding {
        @NotBlank
        @Size(max = 80)
        private String modelCode;
        @JsonProperty("isDefault")
        private Boolean isDefault = false;
        private Boolean userSelectable = false;
        @Min(0)
        private Integer fallbackPriority = 100;
        private Integer status = 1;
    }
}
