package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Add favorite item request")
public class FavoriteItemCreateDto {

    @Schema(description = "Favorite item type: knowledge/blog", example = "knowledge")
    private String itemType;

    @Schema(description = "Target ID, interpreted by itemType", example = "1")
    private Long targetId;

    @Schema(description = "Knowledge ID, kept for backward compatibility", example = "1")
    private Long knowledgeId;

    @Schema(description = "Blog ID", example = "1")
    private Long blogId;
}
